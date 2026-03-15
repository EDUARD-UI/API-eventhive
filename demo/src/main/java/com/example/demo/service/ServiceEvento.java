package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.EventoBusquedaDTO;
import com.example.demo.dto.EventoDTO;
import com.example.demo.dto.EventoDestacadoDTO;
import com.example.demo.dto.EventoDetalleDTO;
import com.example.demo.dto.NombreEventoDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Evento;
import com.example.demo.model.Localidad;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.LocalidadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceEvento {

    private final EventoRepository eventoRepository;
    private final LocalidadRepository localidadRepository;

    //metodo para buscar eventos por titulo escrito por el usuario
    public List<EventoBusquedaDTO> buscarPorTituloParcialDTO(String titulo) {
        return eventoRepository.findByTituloContainingIgnoreCase(titulo).stream()
                .map(e -> new EventoBusquedaDTO(
                        e.getId(), e.getTitulo(),
                        e.getCategoria() != null ? e.getCategoria().getNombre() : "General"))
                .collect(Collectors.toList());
    }

    //metodo para buscar eventos por categoria seleccionada
    public List<EventoDTO> buscarPorCategoriaDTO(Long categoriaId) {
        return eventoRepository.findByCategoriaId(categoriaId).stream()
                .map(e -> {
                    EventoDTO dto = new EventoDTO();
                    dto.setId(e.getId());
                    dto.setTitulo(e.getTitulo());
                    dto.setDescripcion(e.getDescripcion());
                    dto.setLugar(e.getLugar());
                    dto.setFoto(e.getFoto());
                    dto.setFecha(e.getFecha());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    //metodo para mostrar los 3 proximos eventos destacados
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
                    if (e.getCategoria() != null) dto.setCategoriaNombre(e.getCategoria().getNombre());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    //metodo para mostrar el evento seleccionado por el usuario con localidades
    public EventoDetalleDTO obtenerEventoDetalleDTO(Long id) {
        Evento e = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));

        EventoDetalleDTO dto = new EventoDetalleDTO();
        dto.setId(e.getId());
        dto.setTitulo(e.getTitulo());
        dto.setDescripcion(e.getDescripcion());
        dto.setFoto(e.getFoto());
        dto.setFecha(e.getFecha());
        dto.setHora(e.getHora());
        dto.setLugar(e.getLugar());
        if (e.getCategoria() != null) dto.setCategoriaNombre(e.getCategoria().getNombre());
        if (e.getEstado()    != null) dto.setEstadoNombre(e.getEstado().getNombre());

        List<Localidad> localidades = localidadRepository.findByEventoId(id);
        dto.setLocalidades(localidades.stream().map(l -> {
            EventoDetalleDTO.LocalidadInfo li = new EventoDetalleDTO.LocalidadInfo();
            li.setId(l.getId());
            li.setNombre(l.getNombre());
            li.setPrecio(l.getPrecio());
            li.setCapacidad(l.getCapacidad());
            li.setDisponibles(l.getDisponibles());
            return li;
        }).collect(Collectors.toList()));

        return dto;
    }

    public List<NombreEventoDTO> obtenerNombresEventosPorOrganizador(Long organizadorId) {
        return eventoRepository.findNombresByOrganizadorId(organizadorId);
    }
    
    public List<NombreEventoDTO> obtenerNombresEventos() {
        return eventoRepository.findAll().stream()
                .map(e -> new NombreEventoDTO(e.getId(), e.getTitulo()))
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
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));
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

    public long contarPorOrganizador(Long id) {
        return eventoRepository.countByUsuarioId(id);
    }

    public List<Evento> eventosPorCategoria(Long id) {
        return eventoRepository.findByCategoriaId(id);
    }

    public long contarEventosPorCategoria(Long categoriaId) {
        return eventoRepository.countByCategoriaId(categoriaId);
    }
    
}