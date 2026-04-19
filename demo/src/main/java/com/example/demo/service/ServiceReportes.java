package com.example.demo.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.model.Evento;
import com.example.demo.repository.EventoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceReportes {

    private final EventoRepository eventoRepository;

    public Map<String, Long> contarEventosPorCategoria(String organizadorId) {
        List<Evento> eventos = eventoRepository.findByUsuarioId(organizadorId);
        
        return eventos.stream()
                .collect(Collectors.groupingBy(
                    evento -> evento.getCategoria() != null ? evento.getCategoria().getNombre() : "Sin categoría",
                    LinkedHashMap::new,
                    Collectors.counting()
                ));
    }
}