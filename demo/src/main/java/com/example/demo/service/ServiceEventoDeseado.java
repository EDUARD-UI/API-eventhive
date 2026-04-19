package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.EventoDeseadoDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.EventoDeseado;
import com.example.demo.repository.EventoDeseadoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceEventoDeseado {

    private final EventoDeseadoRepository eventoDeseadoRepository;

    public EventoDeseado obtenerEventoDeseadoPorId(String id) {
        return eventoDeseadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento deseado no encontrado"));
    }

    public EventoDeseado guardarEventoDeseado(EventoDeseado eventoDeseado) {
        return eventoDeseadoRepository.save(eventoDeseado);
    }

    public void eliminarEventoDeseado(String id) {
        eventoDeseadoRepository.deleteById(id);
    }

    public Page<EventoDeseadoDTO> obtenerDeseadosDTOPorUsuario(String usuarioId, Pageable pageable) {
        return eventoDeseadoRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::toDTO);
    }

    public List<EventoDeseadoDTO> obtenerDeseadosDTOPorUsuario(String usuarioId) {
        return eventoDeseadoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private EventoDeseadoDTO toDTO(EventoDeseado ed) {
        EventoDeseadoDTO dto = new EventoDeseadoDTO();
        dto.setId(ed.getId());
        if (ed.getEvento() != null) {
            dto.setEventoId(ed.getEvento().getId());
            dto.setEventoTitulo(ed.getEvento().getTitulo());
            dto.setEventoLugar(ed.getEvento().getLugar());
            dto.setEventoFoto(ed.getEvento().getFoto());
        }
        return dto;
    }
}
