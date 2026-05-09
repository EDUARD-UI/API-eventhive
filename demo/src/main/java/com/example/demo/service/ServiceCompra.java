package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.Compra;
import com.example.demo.model.Evento;
import com.example.demo.model.ItemCompra;
import com.example.demo.model.Localidad;
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
    public Compra realizarCompra(List<ItemCompra> items) {
        Usuario usuario = authHelper.usuarioAutenticado();

        BigDecimal total = BigDecimal.ZERO;
        List<ItemCompra> itemsValidados = new ArrayList<>();

        for (ItemCompra item : items) {
            Evento evento = eventoRepository.findById(item.getEventoId())
                .orElseThrow(() -> new BusinessException("Evento no encontrado"));

            Localidad localidad = evento.getLocalidades().stream()
                .filter(l -> l.getId().equals(item.getLocalidadId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Localidad no encontrada"));

            if (localidad.getDisponibles() < item.getCantidad()) {
                throw new BusinessException("No hay suficientes tiquetes");
            }

            localidad.setDisponibles(localidad.getDisponibles() - item.getCantidad());
            item.setPrecioUnitario(localidad.getPrecio());
            total = total.add(localidad.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
            itemsValidados.add(item);
        }

        List<String> eventosActualizados = items.stream()
            .map(ItemCompra::getEventoId)
            .distinct()
            .toList();

        eventoRepository.findAllById(eventosActualizados).forEach(eventoRepository::save);

        Compra compra = new Compra();
        compra.setFechaCompra(LocalDateTime.now());
        compra.setTotal(total);
        compra.setMetodoPago("TARJETA");
        compra.setCliente(usuario);
        compra.setItems(itemsValidados);

        return compraRepository.save(compra);
    }

    @PreAuthorize("isAuthenticated()")
    public void cancelarCompra(String id) {
        Compra compra = obtenerPorId(id);
        Usuario usuario = authHelper.usuarioAutenticado();

        if (!compra.getCliente().getId().equals(usuario.getId())) {
            throw new BusinessException("No autorizado");
        }

        for (ItemCompra item : compra.getItems()) {
            Evento evento = eventoRepository.findById(item.getEventoId())
                .orElseThrow(() -> new BusinessException("Evento no encontrado"));

            evento.getLocalidades().stream()
                .filter(l -> l.getId().equals(item.getLocalidadId()))
                .findFirst()
                .ifPresent(l -> l.setDisponibles(l.getDisponibles() + item.getCantidad()));

            eventoRepository.save(evento);
        }

        compraRepository.deleteById(id);
    }
}
