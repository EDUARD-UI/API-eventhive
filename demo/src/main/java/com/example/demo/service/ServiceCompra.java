package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.Compra;
import com.example.demo.model.Evento;
import com.example.demo.model.ItemCompra;
import com.example.demo.model.Localidad;
import com.example.demo.model.Promocion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CompraRepository;
import com.example.demo.repository.EventoRepository;
import com.example.demo.utils.AuthenticatedUserHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceCompra {

    private final CompraRepository compraRepository;
    private final EventoRepository eventoRepository;
    private final AuthenticatedUserHelper authHelper;

    @PreAuthorize("isAuthenticated()")
    public Page<Compra> listarMisCompras(Pageable pageable) {
        Usuario usuario = authHelper.usuarioAutenticado();
        return compraRepository.findByClienteIdOrderByFechaCompraDesc(usuario.getId(), pageable);
    }

    public Compra obtenerPorId(String id) {
        return compraRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Compra no encontrada"));
    }

    public Compra obtenerCompraPorId(String id) {
        return obtenerPorId(id);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public Compra realizarCompra(List<ItemCompra> items) {
        Usuario usuario = authHelper.usuarioAutenticado();

        BigDecimal total = BigDecimal.ZERO;
        List<ItemCompra> itemsValidados = new ArrayList<>();
        // Cachear eventos modificados para guardarlos al final
        Map<String, Evento> eventosModificados = new HashMap<>();

        for (ItemCompra item : items) {
            Evento evento = eventoRepository.findById(item.getEventoId())
                .orElseThrow(() -> new BusinessException("Evento no encontrado: " + item.getEventoId()));


            boolean localidadesActualizadas = false;
            if (evento.getLocalidades() != null) {
                for (Localidad l : evento.getLocalidades()) {
                    String lid = l.getId();
                    if (lid == null || lid.isBlank() || "null".equals(lid)) {
                        l.setId(new ObjectId().toHexString());
                        localidadesActualizadas = true;
                    }
                }
                if (localidadesActualizadas) {

                    eventoRepository.save(evento);
                }
            }

            // Validación defensiva: el cliente debe enviar un localidadId válido.
            // Si no viene, pero el evento tiene una sola localidad, la inferimos.
            if (item.getLocalidadId() == null || item.getLocalidadId().isBlank() || "null".equals(item.getLocalidadId())) {
                if (evento.getLocalidades() == null || evento.getLocalidades().isEmpty()) {
                    throw new BusinessException("El item no tiene localidadId válido y el evento no tiene localidades: " + item.getLocalidadId());
                } else if (evento.getLocalidades().size() == 1) {
                    // Si solo hay una localidad, la usamos
                    Localidad unica = evento.getLocalidades().get(0);
                    String lid = unica.getId();
                    if (lid == null || lid.isBlank() || "null".equals(lid)) {
                        unica.setId(new ObjectId().toHexString());
                        // persistir el evento para que la localidad tenga id
                        eventoRepository.save(evento);
                    }
                    item.setLocalidadId(unica.getId());
                } else {
                    throw new BusinessException("El item no tiene localidadId válido: " + item.getLocalidadId() + ". Las localidades disponibles son: " + evento.getLocalidades().stream()
                        .map(l -> l.getId() + "=" + l.getNombre())
                        .toList());
                }
            }

            Localidad localidad = evento.getLocalidades().stream()
                .filter(l -> item.getLocalidadId().equals(l.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                    "Localidad '" + item.getLocalidadId() + "' no encontrada en el evento '" + evento.getTitulo() + "'. " +
                    "Las localidades disponibles son: " + evento.getLocalidades().stream()
                        .map(l -> l.getId() + "=" + l.getNombre())
                        .toList()
                ));

            if (localidad.getDisponibles() < item.getCantidad()) {
                throw new BusinessException(
                    "No hay suficientes tiquetes en '" + localidad.getNombre() +
                    "'. Disponibles: " + localidad.getDisponibles() +
                    ", solicitados: " + item.getCantidad()
                );
            }

            // Aplicar promoción si existe y está vigente
            BigDecimal precioUnitario = localidad.getPrecio();
            Promocion promo = evento.getPromocion();
            if (promo != null && promo.getDescuento() != null && promo.getFechaInicio() != null && promo.getFechaFin() != null) {
                LocalDate hoy = LocalDate.now();
                if ((hoy.isEqual(promo.getFechaInicio()) || hoy.isAfter(promo.getFechaInicio())) &&
                    (hoy.isEqual(promo.getFechaFin())   || hoy.isBefore(promo.getFechaFin()))) {
                    BigDecimal porcentaje = BigDecimal.valueOf(promo.getDescuento());
                    // porcentaje está en valores como 10.0 -> 10%
                    precioUnitario = precioUnitario
                        .multiply(BigDecimal.valueOf(100).subtract(porcentaje))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                }
            }

            localidad.setDisponibles(localidad.getDisponibles() - item.getCantidad());
            item.setPrecioUnitario(precioUnitario);
            total = total.add(precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad())));
            itemsValidados.add(item);

            eventosModificados.put(evento.getId(), evento);
        }

        for (Evento ev : eventosModificados.values()) {
            eventoRepository.save(ev);
        }

        Compra compra = new Compra();
        compra.setFechaCompra(LocalDateTime.now());
        compra.setTotal(total);
        compra.setMetodoPago("TARJETA");
        compra.setCliente(usuario);
        compra.setItems(itemsValidados);

        return compraRepository.save(compra);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void cancelarCompra(String id) {
        Compra compra = obtenerPorId(id);
        Usuario usuario = authHelper.usuarioAutenticado();

        if (!compra.getCliente().getId().equals(usuario.getId())) {
            throw new BusinessException("No autorizado para cancelar esta compra");
        }

        for (ItemCompra item : compra.getItems()) {
            Evento evento = eventoRepository.findById(item.getEventoId())
                .orElseThrow(() -> new BusinessException("Evento no encontrado: " + item.getEventoId()));

            // Misma comparación defensiva para la cancelación
            evento.getLocalidades().stream()
                .filter(l -> item.getLocalidadId() != null && item.getLocalidadId().equals(l.getId()))
                .findFirst()
                .ifPresent(l -> l.setDisponibles(l.getDisponibles() + item.getCantidad()));

            eventoRepository.save(evento);
        }

        compraRepository.deleteById(id);
    }
}
