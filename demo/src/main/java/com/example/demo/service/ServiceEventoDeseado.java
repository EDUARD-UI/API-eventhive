package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.EventoDeseado;
import com.example.demo.repository.EventoDeseadoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceEventoDeseado {

    private final EventoDeseadoRepository eventoDeseadoRepository;

    public List<EventoDeseado> getEventosDeseadosPorUsuario(Long usuarioId) {
        return eventoDeseadoRepository.findByUsuarioId(usuarioId);
    }

    public EventoDeseado guardarEventoDeseado(EventoDeseado eventoDeseado) {
        return eventoDeseadoRepository.save(eventoDeseado);
    }

    public EventoDeseado obtenerEventoDeseadoPorId(long id){
        return eventoDeseadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
    }
}

