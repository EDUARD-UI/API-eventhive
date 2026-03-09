package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.EventoBusquedaDTO;
import com.example.demo.dto.EventoDTO;
import com.example.demo.dto.EventoDestacadoDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Evento;
import com.example.demo.repository.EventoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceEvento {

    private final EventoRepository eventoRepository;

    public List<EventoBusquedaDTO> buscarPorTituloParcialDTO(String titulo) {
        List<Evento> eventos = eventoRepository.findByTituloContainingIgnoreCase(titulo);

        return eventos.stream()
                .map(evento -> new EventoBusquedaDTO(
                evento.getId(),
                evento.getTitulo(),
                evento.getCategoria() != null ? evento.getCategoria().getNombre() : "General"
        ))
                .collect(Collectors.toList());
    }

    public List<EventoDTO> buscarPorCategoriaDTO(Long categoriaId) {
        List<Evento> eventos = eventoRepository.findByCategoriaId(categoriaId);

        return eventos.stream()
                .map(evento -> {
                    EventoDTO dto = new EventoDTO();
                    dto.setId(evento.getId());
                    dto.setTitulo(evento.getTitulo());
                    dto.setDescripcion(evento.getDescripcion());
                    dto.setLugar(evento.getLugar());
                    dto.setFoto(evento.getFoto());
                    dto.setFecha(evento.getFecha());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<EventoDestacadoDTO> obtenerTop3EventosDTO() {
        return eventoRepository.findTop3ByOrderByFechaAsc().stream()
                .map(e -> {
                    EventoDestacadoDTO dto = new EventoDestacadoDTO();
                    dto.setId(e.getId());
                    dto.setTitulo(e.getTitulo());
                    dto.setDescripcion(e.getDescripcion());
                    dto.setLugar(e.getLugar());
                    dto.setFoto(e.getFoto());
                    dto.setFecha(e.getFecha());
                    if (e.getCategoria() != null) {
                        dto.setCategoriaNombre(e.getCategoria().getNombre());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<Evento> todosLosEventos() {
        return eventoRepository.findAll();
    }

    public Evento crearEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Evento obtenerEventoPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
    }

    public void actualizarEvento(Evento eventoExistente) {
        eventoRepository.save(eventoExistente);
    }

    public void eliminarEvento(Long id) {
        eventoRepository.deleteById(id);
    }

    public List<Evento> obtenerPorOrganizador(Long id) {
        return eventoRepository.findByUsuarioId(id);
    }

    public List<Evento> eventosPorCategoria(Long id) {
        return eventoRepository.findByCategoriaId(id);
    }

    public long contarEventosPorCategoria(Long categoriaId) {
        return eventoRepository.countByCategoriaId(categoriaId);
    }

}
