package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.Compra;
import com.example.demo.model.Evento;
import com.example.demo.model.ItemCompra;
import com.example.demo.model.Tiquete;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CompraRepository;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.TiqueteRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceCompra {

    private final CompraRepository compraRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final TiqueteRepository tiqueteRepository;

    public List<Compra> listarMisCompras() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        
        return compraRepository.findByClienteId(usuario.getId());
    }

    public Compra obtenerPorId(String id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Compra no encontrada"));
    }

    public Compra obtenerCompraPorId(String id) {
        return obtenerPorId(id);
    }

    public Compra realizarCompra(List<ItemCompra> items) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        
        BigDecimal total = BigDecimal.ZERO;
        List<ItemCompra> itemsValidados = new ArrayList<>();
        
        for (ItemCompra item : items) {
            Tiquete tiquete = tiqueteRepository.findById(item.getTiqueteId())
                    .orElseThrow(() -> new BusinessException("Tiquete no encontrado"));
            
            Evento evento = eventoRepository.findById(tiquete.getEventoId())
                    .orElseThrow(() -> new BusinessException("Evento no encontrado"));
            
            evento.getLocalidades().stream()
                .filter(l -> l.getId().equals(tiquete.getLocalidadId()))
                .findFirst()
                .ifPresent(localidad -> {
                    if (localidad.getDisponibles() < item.getCantidad()) {
                        throw new BusinessException("No hay suficientes tiquetes disponibles");
                    }
                    localidad.setDisponibles(localidad.getDisponibles() - item.getCantidad());
                    eventoRepository.save(evento);
                });
            
            item.setPrecioUnitario(tiquete.getLocalidad().getPrecio());
            total = total.add(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())));
            itemsValidados.add(item);
        }
        
        Compra compra = new Compra();
        compra.setFechaCompra(LocalDateTime.now());
        compra.setTotal(total);
        compra.setMetodoPago("TARJETA");
        compra.setCliente(usuario);
        compra.setItems(itemsValidados);
        
        return compraRepository.save(compra);
    }

    public void cancelarCompra(String id) {
        Compra compra = obtenerPorId(id);
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        
        if (!compra.getCliente().getCorreo().equals(correo)) {
            throw new BusinessException("No tienes permiso para cancelar esta compra");
        }
        
        for (ItemCompra item : compra.getItems()) {
            Tiquete tiquete = tiqueteRepository.findById(item.getTiqueteId())
                    .orElseThrow(() -> new BusinessException("Tiquete no encontrado"));
            
            Evento evento = eventoRepository.findById(tiquete.getEventoId())
                    .orElseThrow(() -> new BusinessException("Evento no encontrado"));
            
            evento.getLocalidades().stream()
                .filter(l -> l.getId().equals(tiquete.getLocalidadId()))
                .findFirst()
                .ifPresent(localidad -> {
                    localidad.setDisponibles(localidad.getDisponibles() + item.getCantidad());
                    eventoRepository.save(evento);
                });
        }
        
        compraRepository.deleteById(id);
    }
}