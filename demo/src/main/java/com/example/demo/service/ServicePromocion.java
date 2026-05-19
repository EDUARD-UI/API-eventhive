package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.demo.dto.PromocionDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Evento;
import com.example.demo.model.Promocion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.PromocionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicePromocion {

    private final PromocionRepository promocionRepository;
    private final EventoRepository eventoRepository;
    private final MongoTemplate mongoTemplate;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public List<Promocion> obtenerPromociones() {
        return promocionRepository.findAll();
    }

    public Page<PromocionDTO> obtenerTodasPromociones(Pageable pageable) {
        return promocionRepository.findAll(pageable).map(this::toDTO);
    }

    public List<Promocion> obtenerPorEvento(String eventoId) {
        return promocionRepository.findByEventosId(eventoId);
    }

    public Promocion obtenerPromocionPorId(String id) {
        return promocionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada"));
    }

    // ── CREAR ─────────────────────────────────────────────────────────────────
    // El ADMINISTRADOR puede crear promociones para cualquier evento.
    // El ORGANIZADOR solo puede para sus propios eventos.
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public void crearPromocion(String eventoId, String descripcion, BigDecimal descuento,
            String fechaInicio, String fechaFin, Usuario organizador) {

        LocalDate fechaInicioLD = LocalDate.parse(fechaInicio, DATE_FORMATTER);
        LocalDate fechaFinLD    = LocalDate.parse(fechaFin,    DATE_FORMATTER);

        if (fechaFinLD.isBefore(fechaInicioLD)) {
            throw new BusinessException("Fecha fin no puede ser anterior a inicio");
        }
        if (descuento.compareTo(BigDecimal.ONE) < 0 || descuento.compareTo(new BigDecimal("75")) > 0) {
            throw new BusinessException("Descuento debe estar entre 1 y 75");
        }

        Evento evento = eventoRepository.findById(eventoId)
            .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

        // Solo validar pertenencia si es ORGANIZADOR (no ADMINISTRADOR)
        boolean esAdmin = organizador.getRol() != null
                && "ADMINISTRADOR".equals(organizador.getRol().getNombre());
        if (!esAdmin && !evento.getOrganizador().getId().equals(organizador.getId())) {
            throw new BusinessException("No autorizado: el evento no te pertenece");
        }

        if (existePromocionParaEvento(eventoId)) {
            throw new BusinessException("Este evento ya tiene una promoción asignada");
        }

        Promocion p = new Promocion();
        p.setDescripcion(descripcion);
        p.setDescuento(descuento.doubleValue());
        p.setFechaInicio(fechaInicioLD);
        p.setFechaFin(fechaFinLD);

        List<Evento> eventos = new ArrayList<>();
        eventos.add(evento);
        p.setEventos(eventos);

        Promocion saved = promocionRepository.save(p);

        evento.setPromocion(saved);
        eventoRepository.save(evento);
    }

    // ── ACTUALIZAR ────────────────────────────────────────────────────────────
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public void actualizarPromocion(String id, String eventoId, String descripcion, BigDecimal descuento,
            String fechaInicio, String fechaFin, Usuario organizador) {

        Promocion p = obtenerPromocionPorId(id);

        boolean esAdmin = organizador.getRol() != null
                && "ADMINISTRADOR".equals(organizador.getRol().getNombre());

        if (!esAdmin) {
            boolean autorizado = p.getEventos() != null && p.getEventos().stream()
                .anyMatch(e -> e.getOrganizador() != null &&
                              e.getOrganizador().getId().equals(organizador.getId()));
            if (!autorizado) {
                throw new BusinessException("No autorizado");
            }
        }

        LocalDate fechaInicioLD = LocalDate.parse(fechaInicio, DATE_FORMATTER);
        LocalDate fechaFinLD    = LocalDate.parse(fechaFin,    DATE_FORMATTER);

        if (fechaFinLD.isBefore(fechaInicioLD)) {
            throw new BusinessException("Fecha fin no puede ser anterior a inicio");
        }
        if (descuento.compareTo(BigDecimal.ONE) < 0 || descuento.compareTo(new BigDecimal("75")) > 0) {
            throw new BusinessException("Descuento debe estar entre 1 y 75");
        }

        if (eventoId != null && !eventoId.isBlank()) {
            Evento nuevoEvento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

            if (!esAdmin && !nuevoEvento.getOrganizador().getId().equals(organizador.getId())) {
                throw new BusinessException("No autorizado: el evento no te pertenece");
            }

            if (p.getEventos() != null) {
                for (Evento evAnterior : p.getEventos()) {
                    evAnterior.setPromocion(null);
                    eventoRepository.save(evAnterior);
                }
            }

            List<Evento> nuevosEventos = new ArrayList<>();
            nuevosEventos.add(nuevoEvento);
            p.setEventos(nuevosEventos);

            nuevoEvento.setPromocion(p);
            eventoRepository.save(nuevoEvento);
        }

        p.setDescripcion(descripcion);
        p.setDescuento(descuento.doubleValue());
        p.setFechaInicio(fechaInicioLD);
        p.setFechaFin(fechaFinLD);

        promocionRepository.save(p);
    }

    // ── ELIMINAR ──────────────────────────────────────────────────────────────
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public void eliminarPromocion(String id, Usuario organizador) {
        Promocion p = obtenerPromocionPorId(id);

        boolean esAdmin = organizador.getRol() != null
                && "ADMINISTRADOR".equals(organizador.getRol().getNombre());

        if (!esAdmin) {
            boolean autorizado = p.getEventos() != null && p.getEventos().stream()
                .anyMatch(e -> e.getOrganizador() != null &&
                              e.getOrganizador().getId().equals(organizador.getId()));
            if (!autorizado) {
                throw new BusinessException("No autorizado");
            }
        }

        if (p.getEventos() != null) {
            for (Evento evento : p.getEventos()) {
                evento.setPromocion(null);
                eventoRepository.save(evento);
            }
        }

        promocionRepository.deleteById(id);
    }

    public Page<PromocionDTO> obtenerDTOPorOrganizador(String organizadorId, Pageable pageable) {
        List<String> eventoIds = eventoRepository.findByOrganizadorId(organizadorId)
            .stream()
            .map(Evento::getId)
            .collect(Collectors.toList());

        if (eventoIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        List<ObjectId> objectIds = eventoIds.stream()
            .map(ObjectId::new)
            .collect(Collectors.toList());

        Query query = new Query(Criteria.where("eventos.$id").in(objectIds));
        long total = mongoTemplate.count(query, Promocion.class);

        query.with(pageable);
        List<Promocion> promociones = mongoTemplate.find(query, Promocion.class);

        List<PromocionDTO> dtos = promociones.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, total);
    }

    private boolean existePromocionParaEvento(String eventoId) {
        try {
            ObjectId oid = new ObjectId(eventoId);
            Query query = new Query(Criteria.where("eventos.$id").is(oid));
            return mongoTemplate.exists(query, Promocion.class);
        } catch (Exception e) {
            return promocionRepository.findFirstByEventosId(eventoId) != null;
        }
    }

    private PromocionDTO toDTO(Promocion p) {
        PromocionDTO dto = new PromocionDTO();
        dto.setId(p.getId());
        dto.setDescripcion(p.getDescripcion());
        dto.setDescuento(BigDecimal.valueOf(p.getDescuento()));
        dto.setFechaInicio(p.getFechaInicio());
        dto.setFechaFinal(p.getFechaFin());
        if (p.getEventos() != null && !p.getEventos().isEmpty()) {
            Evento ev = p.getEventos().get(0);
            if (ev != null) {
                dto.setEventoId(ev.getId());
                dto.setEventoTitulo(ev.getTitulo());
            } else {
                dto.setEventoId(null);
                dto.setEventoTitulo("Evento no asignado");
            }
        } else {
            dto.setEventoId(null);
            dto.setEventoTitulo("Evento no asignado");
        }
        return dto;
    }
}
