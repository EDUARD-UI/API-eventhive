package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.Evento;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceEvento {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    public Evento obtenerPorId(String id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Evento no encontrado"));
    }

    public List<com.example.demo.model.Localidad> obtenerLocalidades(String eventoId) {
        Evento evento = obtenerPorId(eventoId);
        return evento.getLocalidades();
    }

    public Evento crearEvento(Evento evento) {
        String correo = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario organizador = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        
        evento.setOrganizador(organizador);
        return eventoRepository.save(evento);
    }

    public Evento actualizarEvento(String id, Evento eventoActualizado) {
        Evento evento = obtenerPorId(id);
        verificarPermiso(evento);
        
        evento.setTitulo(eventoActualizado.getTitulo());
        evento.setDescripcion(eventoActualizado.getDescripcion());
        evento.setFecha(eventoActualizado.getFecha());
        evento.setHora(eventoActualizado.getHora());
        evento.setLugar(eventoActualizado.getLugar());
        evento.setEstado(eventoActualizado.getEstado());
        evento.setCategoria(eventoActualizado.getCategoria());
        
        return eventoRepository.save(evento);
    }

    public void eliminarEvento(String id) {
        Evento evento = obtenerPorId(id);
        verificarPermiso(evento);
        eventoRepository.deleteById(id);
    }

    public Evento agregarLocalidad(String eventoId, com.example.demo.model.Localidad localidad) {
        Evento evento = obtenerPorId(eventoId);
        verificarPermiso(evento);
        
        evento.getLocalidades().add(localidad);
        return eventoRepository.save(evento);
    }

    public Evento actualizarLocalidad(String eventoId, int index, com.example.demo.model.Localidad localidadActualizada) {
        Evento evento = obtenerPorId(eventoId);
        verificarPermiso(evento);
        
        if (index < 0 || index >= evento.getLocalidades().size()) {
            throw new BusinessException("Índice de localidad inválido");
        }
        
        com.example.demo.model.Localidad localidad = evento.getLocalidades().get(index);
        localidad.setNombre(localidadActualizada.getNombre());
        localidad.setPrecio(localidadActualizada.getPrecio());
        localidad.setCapacidad(localidadActualizada.getCapacidad());
        localidad.setDisponibles(localidadActualizada.getDisponibles());
        
        return eventoRepository.save(evento);
    }

    public Evento eliminarLocalidad(String eventoId, int index) {
        Evento evento = obtenerPorId(eventoId);
        verificarPermiso(evento);
        
        if (index < 0 || index >= evento.getLocalidades().size()) {
            throw new BusinessException("Índice de localidad inválido");
        }
        
        evento.getLocalidades().remove(index);
        return eventoRepository.save(evento);
    }

    public void agregarEventoDeseado(String eventoId) {
        String correo = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        
        if (usuario.getEventosDeseadosIds() == null) {
            usuario.setEventosDeseadosIds(new java.util.ArrayList<>());
        }
        
        if (!usuario.getEventosDeseadosIds().contains(eventoId)) {
            usuario.getEventosDeseadosIds().add(eventoId);
            usuarioRepository.save(usuario);
        }
    }

    public void eliminarEventoDeseado(String eventoId) {
        String correo = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        
        if (usuario.getEventosDeseadosIds() != null) {
            usuario.getEventosDeseadosIds().remove(eventoId);
            usuarioRepository.save(usuario);
        }
    }

    public List<Evento> buscarPorCategoriaDTO(String categoriaId) {
        return eventoRepository.findByCategoriaId(categoriaId);
    }

    public long contarEventosPorCategoria(String categoriaId) {
        return eventoRepository.countByCategoriaId(categoriaId);
    }

    private void verificarPermiso(Evento evento) {
        String correo = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        
        boolean isAdmin = usuario.getRol().getNombre().equals("ADMINISTRADOR");
        boolean esOrganizador = evento.getOrganizador().getCorreo().equals(correo);
        
        if (!isAdmin && !esOrganizador) {
            throw new BusinessException("No tienes permiso para modificar este evento");
        }
    }
}