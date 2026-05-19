package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.Compra;
import com.example.demo.model.Evento;
import com.example.demo.model.ItemCompra;
import com.example.demo.model.Localidad;
import com.example.demo.model.Promocion;
import com.example.demo.model.Tiquete;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CompraRepository;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.TiqueteRepository;
import com.example.demo.utils.AuthenticatedUserHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceCompra {

    private final CompraRepository compraRepository;
    private final EventoRepository eventoRepository;
    private final TiqueteRepository tiqueteRepository;
    private final AuthenticatedUserHelper authHelper;
    // ── FIX BUG 2: inyectar MongoTemplate para actualizaciones parciales ──────
    private final MongoTemplate mongoTemplate;

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
        Map<String, List<Localidad>> localidadesActualizadasPorEvento = new HashMap<>();
        Map<String, Evento> eventosModificados = new HashMap<>();

        for (ItemCompra item : items) {
            Evento evento = eventoRepository.findByIdWithReferences(item.getEventoId());
            if (evento == null) {
                throw new BusinessException("Evento no encontrado: " + item.getEventoId());
            }

            // Asegurar que todas las localidades tengan id
            boolean localidadesActualizadas = false;
            if (evento.getLocalidades() != null) {
                for (Localidad l : evento.getLocalidades()) {
                    if (l.getId() == null || l.getId().isBlank() || "null".equals(l.getId())) {
                        l.setId(new ObjectId().toHexString());
                        localidadesActualizadas = true;
                    }
                }
                if (localidadesActualizadas) {
                    actualizarSoloLocalidades(evento.getId(), evento.getLocalidades());
                }
            }

            // Inferir localidad si no se envió
            if (item.getLocalidadId() == null || item.getLocalidadId().isBlank()
                    || "null".equals(item.getLocalidadId())) {
                if (evento.getLocalidades() == null || evento.getLocalidades().isEmpty()) {
                    throw new BusinessException(
                        "El item no tiene localidadId válido y el evento no tiene localidades.");
                } else if (evento.getLocalidades().size() == 1) {
                    Localidad unica = evento.getLocalidades().get(0);
                    if (unica.getId() == null || unica.getId().isBlank() || "null".equals(unica.getId())) {
                        unica.setId(new ObjectId().toHexString());
                        actualizarSoloLocalidades(evento.getId(), evento.getLocalidades());
                    }
                    item.setLocalidadId(unica.getId());
                } else {
                    String disponibles = evento.getLocalidades().stream()
                        .map(l -> l.getNombre() + " (" + l.getId() + ")")
                        .collect(Collectors.joining(", "));
                    throw new BusinessException(
                        "El item no tiene localidadId válido para el evento '" + evento.getTitulo()
                        + "'. Localidades disponibles: " + disponibles
                    );
                }
            }

            Localidad localidad = evento.getLocalidades().stream()
                .filter(l -> item.getLocalidadId().equals(l.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                    "Localidad '" + item.getLocalidadId() + "' no encontrada en el evento '"
                    + evento.getTitulo() + "'."
                ));

            if (localidad.getDisponibles() < item.getCantidad()) {
                throw new BusinessException(
                    "No hay suficientes tiquetes en '" + localidad.getNombre() +
                    "'. Disponibles: " + localidad.getDisponibles() +
                    ", solicitados: " + item.getCantidad()
                );
            }

            // Aplicar promoción si está vigente
            BigDecimal precioUnitario = localidad.getPrecio();
            Promocion promo = evento.getPromocion();
            if (promo != null && promo.getDescuento() != null
                    && promo.getFechaInicio() != null && promo.getFechaFin() != null) {
                LocalDate hoy = LocalDate.now();
                if ((hoy.isEqual(promo.getFechaInicio()) || hoy.isAfter(promo.getFechaInicio())) &&
                    (hoy.isEqual(promo.getFechaFin())   || hoy.isBefore(promo.getFechaFin()))) {
                    BigDecimal porcentaje = BigDecimal.valueOf(promo.getDescuento());
                    precioUnitario = precioUnitario
                        .multiply(BigDecimal.valueOf(100).subtract(porcentaje))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                }
            }

            // Descontar disponibles en memoria
            localidad.setDisponibles(localidad.getDisponibles() - item.getCantidad());
            item.setPrecioUnitario(precioUnitario);
            total = total.add(precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad())));
            itemsValidados.add(item);

            // Acumular localidades modificadas y evento para generar tiquetes después
            localidadesActualizadasPorEvento.put(evento.getId(), evento.getLocalidades());
            eventosModificados.put(evento.getId(), evento);
        }

        for (Map.Entry<String, List<Localidad>> entry : localidadesActualizadasPorEvento.entrySet()) {
            actualizarSoloLocalidades(entry.getKey(), entry.getValue());
        }

        // Guardar la compra
        Compra compra = new Compra();
        compra.setFechaCompra(LocalDateTime.now());
        compra.setTotal(total);
        compra.setMetodoPago("TARJETA");
        compra.setCliente(usuario);
        compra.setItems(itemsValidados);
        Compra compraGuardada = compraRepository.save(compra);

        // ── GENERAR TIQUETES ─────────────────────────────────────────────────
        for (ItemCompra item : itemsValidados) {
            Evento evento = eventosModificados.get(item.getEventoId());
            for (int i = 0; i < item.getCantidad(); i++) {
                Tiquete tiquete = new Tiquete();
                tiquete.setCodigoQR(UUID.randomUUID().toString());
                tiquete.setLocalidadId(item.getLocalidadId());
                tiquete.setEvento(evento);
                tiquete.setCompra(compraGuardada);
                tiqueteRepository.save(tiquete);
            }
        }

        return compraGuardada;
    }

    private void actualizarSoloLocalidades(String eventoId, List<Localidad> localidades) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(eventoId)));
        Update update = new Update().set("localidades", localidades);
        mongoTemplate.updateFirst(query, update, Evento.class);
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

            evento.getLocalidades().stream()
                .filter(l -> item.getLocalidadId() != null && item.getLocalidadId().equals(l.getId()))
                .findFirst()
                .ifPresent(l -> l.setDisponibles(l.getDisponibles() + item.getCantidad()));

            // ── FIX BUG 2: también usar actualización parcial al cancelar ────
            actualizarSoloLocalidades(evento.getId(), evento.getLocalidades());
        }

        compraRepository.deleteById(id);
    }
}
