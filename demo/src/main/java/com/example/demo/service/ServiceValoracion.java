package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ValoracionDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Evento;
import com.example.demo.model.Usuario;
import com.example.demo.model.Valoracion;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.ValoracionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceValoracion {

    private final ValoracionRepository valoracionRepository;
    private final EventoRepository eventoRepository;

    public Valoracion obtenerValoracionPorId(String id) {
        return valoracionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoracion no encontrada con id: " + id));
    }

    public List<Valoracion> obtenerTop3Valoraciones() {
        return valoracionRepository.findTop3ByOrderById();
    }

    public List<Valoracion> obtenerTodasLasValoraciones() {
        return valoracionRepository.findAll();
    }

    public long contarValoracionesPorUsuario(String usuarioId) {
        return valoracionRepository.countByClienteId(usuarioId);
    }

    public Page<ValoracionDTO> obtenerValoracionesDTOPorUsuario(String usuarioId, Pageable pageable) {
        return valoracionRepository.findByClienteIdOrderByIdDesc(usuarioId, pageable)
                .map(this::toDTO);
    }

    public Page<ValoracionDTO> obtenerValoracionesDTOPorEvento(String eventoId, Pageable pageable) {
        return valoracionRepository.findByEventoIdOrderByIdDesc(eventoId, pageable)
                .map(this::toDTO);
    }

    public List<ValoracionDTO> obtenerValoracionesDTOPorUsuario(String usuarioId) {
        return valoracionRepository.findByClienteIdOrderByIdDesc(usuarioId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ValoracionDTO> obtenerValoracionesDTOPorEvento(String eventoId) {
        return valoracionRepository.findByEventoIdOrderByIdDesc(eventoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void crearValoracion(Usuario cliente, String eventoId, String comentario, long calificacion) {
        validarCalificacion(calificacion);

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

        Valoracion v = new Valoracion();
        v.setCliente(cliente);
        v.setEvento(evento);
        v.setComentario(comentario);
        v.setCalificacion(calificacion);
        valoracionRepository.save(v);
    }

    public void actualizarValoracion(String id, Usuario cliente, String comentario, long calificacion) {
        validarCalificacion(calificacion);

        Valoracion v = obtenerValoracionVerificada(id, cliente);
        v.setComentario(comentario);
        v.setCalificacion(calificacion);
        valoracionRepository.save(v);
    }

    public void eliminarValoracion(String id, Usuario cliente) {
        obtenerValoracionVerificada(id, cliente);
        valoracionRepository.deleteById(id);
    }

    private void validarCalificacion(long calificacion) {
        if (calificacion < 1 || calificacion > 5) {
            throw new BusinessException("La calificación debe estar entre 1 y 5");
        }
    }

    private Valoracion obtenerValoracionVerificada(String id, Usuario cliente) {
        Valoracion v = obtenerValoracionPorId(id);
        if (v == null) {
            throw new ResourceNotFoundException("Valoración no encontrada");
        }
        if (v.getCliente() == null || !v.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException("No autorizado para modificar esta valoración");
        }
        return v;
    }

    //conversion a DTO
    private ValoracionDTO toDTO(Valoracion v) {
        ValoracionDTO dto = new ValoracionDTO();
        dto.setId(v.getId());
        dto.setComentario(v.getComentario());
        dto.setCalificacion(v.getCalificacion());
        if (v.getEvento() != null) {
            dto.setEventoId(v.getEvento().getId());
            dto.setEventoTitulo(v.getEvento().getTitulo());
        }
        return dto;
    }
}
