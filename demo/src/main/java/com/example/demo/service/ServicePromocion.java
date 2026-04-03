package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.PromocionDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Evento;
import com.example.demo.model.Promocion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PromocionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicePromocion {

    private final PromocionRepository promocionRepository;
    private final ServiceEvento serviceEvento;

    public List<Promocion> obtenerPromociones()                    { return promocionRepository.findAll(); }
    public List<Promocion> obtenerPorOrganizador(Long orgId)       { return promocionRepository.findByEventoUsuarioId(orgId); }
    public long contarPorOrganizador(Long orgId)                   { return promocionRepository.countByEventoUsuarioId(orgId); }
    public List<Promocion> obtenerPorEvento(Long eventoId)         { return promocionRepository.findByEventoId(eventoId); }

    public Promocion obtenerPromocionPorId(Long id) {
        return promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada con id: " + id));
    }

    // valida, construye y guarda la promoción
    public void crearPromocion(Long eventoId, String descripcion, BigDecimal descuento,
                                String fechaInicio, String fechaFin, Usuario organizador) {
        validarDescuento(descuento);
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin    = LocalDate.parse(fechaFin);
        if (fin.isBefore(inicio))
            throw new BusinessException("La fecha de fin no puede ser anterior a la de inicio");

        Evento evento = serviceEvento.obtenerEventoPorId(eventoId);
        if (!evento.getUsuario().getId().equals(organizador.getId()))
            throw new BusinessException("No puede crear promociones para eventos de otro organizador");

        Promocion p = new Promocion();
        p.setEvento(evento);
        p.setDescripcion(descripcion);
        p.setDescuento(descuento);
        p.setFechaInicio(inicio);
        p.setFechaFinal(fin);
        promocionRepository.save(p);
    }

    // valida permisos, actualiza y guarda
    public void actualizarPromocion(Long id, Long eventoId, String descripcion, BigDecimal descuento,
                                     String fechaInicio, String fechaFin, Usuario organizador) {
        Promocion p = obtenerPromocionPorId(id);
        if (!p.getEvento().getUsuario().getId().equals(organizador.getId()))
            throw new BusinessException("No autorizado");

        validarDescuento(descuento);
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin    = LocalDate.parse(fechaFin);
        if (fin.isBefore(inicio))
            throw new BusinessException("La fecha de fin no puede ser anterior a la de inicio");

        p.setEvento(serviceEvento.obtenerEventoPorId(eventoId));
        p.setDescripcion(descripcion);
        p.setDescuento(descuento);
        p.setFechaInicio(inicio);
        p.setFechaFinal(fin);
        promocionRepository.save(p);
    }

    // valida permisos y elimina
    public void eliminarPromocion(Long id, Usuario organizador) {
        Promocion p = obtenerPromocionPorId(id);
        if (!p.getEvento().getUsuario().getId().equals(organizador.getId()))
            throw new BusinessException("No autorizado");
        promocionRepository.deleteById(id);
    }

    // conversión a DTO
    public List<PromocionDTO> obtenerDTOPorOrganizador(Long orgId) {
        return obtenerPorOrganizador(orgId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PromocionDTO toDTO(Promocion p) {
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

    private void validarDescuento(BigDecimal d) {
        if (d.compareTo(BigDecimal.ONE) < 0 || d.compareTo(new BigDecimal("75")) > 0)
            throw new BusinessException("El descuento debe estar entre 1 y 75");
    }
}