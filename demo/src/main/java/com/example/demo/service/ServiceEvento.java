package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.Evento;
import com.example.demo.model.Localidad;
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

    public Page<Evento> listarTodos(Pageable pageable) {
        return eventoRepository.findAll(pageable);
    }

    public Evento obtenerPorId(String id) {
        return eventoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Evento no encontrado"));
    }

    public List<Localidad> obtenerLocalidades(String eventoId) {
        return obtenerPorId(eventoId).getLocalidades();
    }

    public Evento crearEvento(Evento evento) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario organizador = usuarioRepository.findByCorreo(correo);
        if (organizador == null) throw new BusinessException("Usuario no encontrado");

        evento.setOrganizador(organizador);
        if (evento.getLocalidades() == null) evento.setLocalidades(new ArrayList<>());
        if (evento.getPromociones() == null) evento.setPromociones(new ArrayList<>());

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

    public Evento agregarLocalidad(String eventoId, Localidad localidad) {
        Evento evento = obtenerPorId(eventoId);
        verificarPermiso(evento);

        if (evento.getLocalidades() == null) evento.setLocalidades(new ArrayList<>());
        evento.getLocalidades().add(localidad);

        return eventoRepository.save(evento);
    }

    public Evento actualizarLocalidad(String eventoId, int index, Localidad actualizada) {
        Evento evento = obtenerPorId(eventoId);
        verificarPermiso(evento);

        if (index < 0 || index >= evento.getLocalidades().size()) {
            throw new BusinessException("Índice inválido");
        }

        Localidad loc = evento.getLocalidades().get(index);
        loc.setNombre(actualizada.getNombre());
        loc.setPrecio(actualizada.getPrecio());
        loc.setCapacidad(actualizada.getCapacidad());
        loc.setDisponibles(actualizada.getDisponibles());

        return eventoRepository.save(evento);
    }

    public Evento eliminarLocalidad(String eventoId, int index) {
        Evento evento = obtenerPorId(eventoId);
        verificarPermiso(evento);

        if (index < 0 || index >= evento.getLocalidades().size()) {
            throw new BusinessException("Índice inválido");
        }

        evento.getLocalidades().remove(index);
        return eventoRepository.save(evento);
    }

    public void agregarEventoDeseado(String eventoId) {
    String correo = SecurityContextHolder.getContext().getAuthentication().getName();
    Usuario usuario = usuarioRepository.findByCorreo(correo);
    if (usuario == null) throw new BusinessException("Usuario no encontrado");

    Evento evento = eventoRepository.findById(eventoId)
        .orElseThrow(() -> new BusinessException("Evento no encontrado"));

    if (usuario.getEventosDeseados() == null) {
        usuario.setEventosDeseados(new ArrayList<>());
    }

    if (!usuario.getEventosDeseados().contains(evento)) {
        usuario.getEventosDeseados().add(evento);
        usuarioRepository.save(usuario);
    }
}

    public void eliminarEventoDeseado(String eventoId) {
    String correo = SecurityContextHolder.getContext().getAuthentication().getName();
    Usuario usuario = usuarioRepository.findByCorreo(correo);
    if (usuario == null) throw new BusinessException("Usuario no encontrado");

    if (usuario.getEventosDeseados() != null) {
        usuario.getEventosDeseados().removeIf(e -> e.getId().equals(eventoId));
        usuarioRepository.save(usuario);
    }
}

    public List<Evento> buscarPorCategoria(String categoriaId) {
        return eventoRepository.findByCategoriaId(categoriaId);
    }

    public long contarEventosPorCategoria(String categoriaId) {
        return eventoRepository.countByCategoriaId(categoriaId);
    }

    private void verificarPermiso(Evento evento) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) throw new BusinessException("Usuario no encontrado");

        boolean esAdmin = usuario.getRol().getNombre().equals("ADMINISTRADOR");
        boolean esOrganizador = evento.getOrganizador().getCorreo().equals(correo);

        if (!esAdmin && !esOrganizador) {
            throw new BusinessException("No autorizado");
        }
    }
}
