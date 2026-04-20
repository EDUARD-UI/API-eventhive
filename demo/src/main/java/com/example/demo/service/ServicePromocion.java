package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.PromocionDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Evento;
import com.example.demo.model.Promocion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.PromocionRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicePromocion {

    private final PromocionRepository promocionRepository;
    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public List<Promocion> obtenerPromociones() {
        return promocionRepository.findAll();
    }

    public List<Promocion> obtenerPorEvento(String eventoId) {
        return promocionRepository.findByEventoId(eventoId);
    }

    public Promocion obtenerPromocionPorId(String id) {
        return promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada"));
    }

    public void crearPromocion(String eventoId, String descripcion, BigDecimal descuento,
            String fechaInicio, String fechaFin, Usuario organizador) {
        
        // Convertir strings a LocalDate
        LocalDate fechaInicioLD = LocalDate.parse(fechaInicio, DATE_FORMATTER);
        LocalDate fechaFinLD = LocalDate.parse(fechaFin, DATE_FORMATTER);
        
        if (fechaFinLD.isBefore(fechaInicioLD)) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la de inicio");
        }
        
        if (descuento.compareTo(BigDecimal.ONE) < 0 || descuento.compareTo(new BigDecimal("75")) > 0) {
            throw new BusinessException("El descuento debe estar entre 1 y 75");
        }

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        
        if (!evento.getOrganizador().getId().equals(organizador.getId())) {
            throw new BusinessException("No puede crear promociones para eventos de otro organizador");
        }

        Promocion p = new Promocion();
        p.setEvento(evento);
        p.setDescripcion(descripcion);
        p.setDescuento(descuento);
        p.setFechaInicio(fechaInicioLD);
        p.setFechaFinal(fechaFinLD);
        promocionRepository.save(p);
    }

    public void eliminarPromocion(String id, Usuario organizador) {
        Promocion p = obtenerPromocionPorId(id);
        if (!p.getEvento().getOrganizador().getId().equals(organizador.getId())) {
            throw new BusinessException("No autorizado");
        }
        promocionRepository.deleteById(id);
    }

    public void actualizarPromocion(String id, String eventoId, String descripcion, BigDecimal descuento,
            String fechaInicio, String fechaFin, Usuario organizador) {
        
        Promocion p = obtenerPromocionPorId(id);
        
        // Validar autorización
        if (!p.getEvento().getOrganizador().getId().equals(organizador.getId())) {
            throw new BusinessException("No autorizado");
        }
        
        // Convertir strings a LocalDate
        LocalDate fechaInicioLD = LocalDate.parse(fechaInicio, DATE_FORMATTER);
        LocalDate fechaFinLD = LocalDate.parse(fechaFin, DATE_FORMATTER);
        
        if (fechaFinLD.isBefore(fechaInicioLD)) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la de inicio");
        }
        
        if (descuento.compareTo(BigDecimal.ONE) < 0 || descuento.compareTo(new BigDecimal("75")) > 0) {
            throw new BusinessException("El descuento debe estar entre 1 y 75");
        }

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        
        if (!evento.getOrganizador().getId().equals(organizador.getId())) {
            throw new BusinessException("No puede actualizar promociones para eventos de otro organizador");
        }

        p.setEvento(evento);
        p.setDescripcion(descripcion);
        p.setDescuento(descuento);
        p.setFechaInicio(fechaInicioLD);
        p.setFechaFinal(fechaFinLD);
        promocionRepository.save(p);
    }

    public Page<PromocionDTO> obtenerDTOPorOrganizador(String organizadorId, Pageable pageable) {
        Usuario organizador = usuarioRepository.findById(organizadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador no encontrado"));
        
        // Obtener eventos del organizador
        List<String> eventoIds = eventoRepository.findByOrganizadorId(organizadorId).stream()
                .map(Evento::getId)
                .toList();
        
        if (eventoIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }
        
        // Obtener promociones de esos eventos
        Page<Promocion> promociones = promocionRepository.findByEventoIdIn(eventoIds, pageable);
        
        // Convertir a DTO
        return promociones.map(this::toDTO);
    }

    private PromocionDTO toDTO(Promocion p) {
        PromocionDTO dto = new PromocionDTO();
        dto.setId(p.getId());
        dto.setDescripcion(p.getDescripcion());
        dto.setDescuento(p.getDescuento());
        dto.setFechaInicio(p.getFechaInicio());
        dto.setFechaFinal(p.getFechaFinal());
        if (p.getEvento() != null) {
            dto.setEventoId(p.getEvento().getId());
            dto.setEventoTitulo(p.getEvento().getTitulo());
        }
        return dto;
    }
}