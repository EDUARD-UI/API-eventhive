package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ValoracionDTO;
import com.example.demo.model.Valoracion;
import com.example.demo.repository.ValoracionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceValoracion {

    private final ValoracionRepository valoracionRepository;

    public void crear(Valoracion valoracion) {
        valoracionRepository.save(valoracion);
    }

    public Valoracion actualizarValoracion(Valoracion valoracion) {
        return valoracionRepository.save(valoracion);
    }

    public void eliminarValoracion(Long id) {
        valoracionRepository.deleteById(id);
    }

    public Valoracion obtenerValoracionPorId(Long id) {
        return valoracionRepository.findById(id).orElse(null);
    }

    public List<Valoracion> obtenerTop3Valoraciones() {
        return valoracionRepository.findTop3ByOrderById();
    }

    public List<Valoracion> obtenerTodasLasValoraciones() {
        return valoracionRepository.findAll();
    }

    // valoraciones por usuario
    public List<ValoracionDTO> obtenerValoracionesDTOPorUsuario(Long usuarioId) {
        return valoracionRepository.findByClienteIdOrderByIdDesc(usuarioId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Valoraciones por evento
    public List<ValoracionDTO> obtenerValoracionesDTOPorEvento(Long eventoId) {
        return valoracionRepository.findByEventoIdOrderByIdDesc(eventoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Long contarValoracionesPorUsuario(Long usuarioId) {
        return valoracionRepository.countByClienteId(usuarioId);
    }

    //convercion a DTO
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