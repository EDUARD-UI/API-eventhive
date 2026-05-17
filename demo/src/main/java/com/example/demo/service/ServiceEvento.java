package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.demo.dto.EventoBusquedaDTO;
import com.example.demo.dto.EventoDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Evento;
import com.example.demo.model.Localidad;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CategoriasRepository;
import com.example.demo.repository.EstadoRepository;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.utils.AuthenticatedUserHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceEvento {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriasRepository categoriasRepository;
    private final EstadoRepository estadoRepository;
    private final AuthenticatedUserHelper authHelper;

    private void resolverCategoria(Evento evento) {
        if (evento.getCategoria() == null) return;
        if (evento.getCategoria().getNombre() != null && !evento.getCategoria().getNombre().isBlank()) return;
        String catId = evento.getCategoria().getId();
        if (catId == null) return;
        categoriasRepository.findById(catId).ifPresent(cat -> evento.getCategoria().setNombre(cat.getNombre()));
    }

    private void resolverEstado(Evento evento) {
        if (evento.getEstado() == null) return;
        if (evento.getEstado().getNombre() != null && !evento.getEstado().getNombre().isBlank()) return;
        String estId = evento.getEstado().getId();
        if (estId == null) return;
        estadoRepository.findById(estId).ifPresent(est -> evento.getEstado().setNombre(est.getNombre()));
    }

    private void resolverOrganizador(Evento evento) {
        if (evento.getOrganizador() == null) return;
        if (evento.getOrganizador().getNombre() != null && !evento.getOrganizador().getNombre().isBlank()) return;
        String orgId = evento.getOrganizador().getId();
        if (orgId == null) return;
        usuarioRepository.findById(orgId).ifPresent(evento::setOrganizador);
    }

    public void resolverReferencias(Evento evento) {
        resolverCategoria(evento);
        resolverEstado(evento);
        resolverOrganizador(evento);
    }

    //Consultas
    public Page<Evento> listarTodos(Pageable pageable) {
        Page<Evento> page = eventoRepository.findAll(pageable);
        page.getContent().forEach(this::resolverReferencias);
        return page;
    }

    public Page<Evento> listarPorOrganizador(String organizadorId, Pageable pageable) {
        Page<Evento> page = eventoRepository.findByOrganizadorId(organizadorId, pageable);
        page.getContent().forEach(this::resolverReferencias);
        return page;
    }

    public Page<Evento> buscarPorOrganizadorYTitulo(String organizadorId, String titulo, Pageable pageable) {
        Page<Evento> page = eventoRepository
                .findByOrganizadorIdAndTituloContainingIgnoreCase(organizadorId, titulo, pageable);
        page.getContent().forEach(this::resolverReferencias);
        return page;
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Page<Evento> buscarAdmin(String titulo, String categoriaId, String estadoId, Pageable pageable) {
        if (titulo != null && !titulo.trim().isEmpty()) {
            Page<Evento> page = eventoRepository.findByTituloContainingIgnoreCase(titulo.trim(), pageable);
            page.getContent().forEach(this::resolverReferencias);
            return page;
        }
        if (categoriaId != null && !categoriaId.isEmpty()) {
            Page<Evento> page = eventoRepository.findByCategoriaId(categoriaId, pageable);
            page.getContent().forEach(this::resolverReferencias);
            return page;
        }
        if (estadoId != null && !estadoId.isEmpty()) {
            Page<Evento> page = eventoRepository.findByEstadoId(estadoId, pageable);
            page.getContent().forEach(this::resolverReferencias);
            return page;
        }
        return listarTodos(pageable);
    }

    public Evento obtenerPorId(String id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Evento no encontrado"));
        resolverReferencias(evento);
        return evento;
    }

    public List<Localidad> obtenerLocalidades(String eventoId) {
        return obtenerPorId(eventoId).getLocalidades();
    }

    //CRUD
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public Evento crearEvento(Evento evento) {
        Usuario organizador = authHelper.usuarioAutenticado();
        evento.setOrganizador(organizador);
        if (evento.getLocalidades() == null) evento.setLocalidades(new ArrayList<>());
        evento.setPromocion(null);
        if (evento.getFoto() != null && evento.getFoto().isBlank()) evento.setFoto(null);
        denormalizarCategoria(evento);
        denormalizarEstado(evento);
        return eventoRepository.save(evento);
    }

    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
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
        if (eventoActualizado.getFoto() != null && !eventoActualizado.getFoto().isBlank()) {
            evento.setFoto(eventoActualizado.getFoto());
        } else if (eventoActualizado.getFoto() != null && eventoActualizado.getFoto().isBlank()) {
            evento.setFoto(null);
        }
        denormalizarCategoria(evento);
        denormalizarEstado(evento);
        return eventoRepository.save(evento);
    }

    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public void eliminarEvento(String id) {
        Evento evento = obtenerPorId(id);
        verificarPermiso(evento);
        eventoRepository.deleteById(id);
    }

    //Localidades
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public Evento agregarLocalidad(String eventoId, Localidad localidad) {
        Evento evento = obtenerPorId(eventoId);
        verificarPermiso(evento);
        if (evento.getLocalidades() == null) evento.setLocalidades(new ArrayList<>());
        evento.getLocalidades().add(localidad);
        return eventoRepository.save(evento);
    }

    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public Evento actualizarLocalidad(String eventoId, int index, Localidad actualizada) {
        Evento evento = obtenerPorId(eventoId);
        verificarPermiso(evento);
        if (index < 0 || index >= evento.getLocalidades().size()) throw new BusinessException("Índice inválido");
        Localidad loc = evento.getLocalidades().get(index);
        loc.setNombre(actualizada.getNombre());
        loc.setPrecio(actualizada.getPrecio());
        loc.setCapacidad(actualizada.getCapacidad());
        loc.setDisponibles(actualizada.getDisponibles());
        return eventoRepository.save(evento);
    }

    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public Evento eliminarLocalidad(String eventoId, int index) {
        Evento evento = obtenerPorId(eventoId);
        verificarPermiso(evento);
        if (index < 0 || index >= evento.getLocalidades().size()) throw new BusinessException("Índice inválido");
        evento.getLocalidades().remove(index);
        return eventoRepository.save(evento);
    }

    //Deseados
    @PreAuthorize("isAuthenticated()")
    public void agregarEventoDeseado(String eventoId) {
        Usuario usuario = authHelper.usuarioAutenticado();
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new BusinessException("Evento no encontrado"));
        if (usuario.getEventosDeseados() == null) usuario.setEventosDeseados(new ArrayList<>());
        if (!usuario.getEventosDeseados().contains(evento)) {
            usuario.getEventosDeseados().add(evento);
            usuarioRepository.save(usuario);
        }
    }

    @PreAuthorize("isAuthenticated()")
    public void eliminarEventoDeseado(String eventoId) {
        Usuario usuario = authHelper.usuarioAutenticado();
        if (usuario.getEventosDeseados() != null) {
            usuario.getEventosDeseados().removeIf(e -> e.getId().equals(eventoId));
            usuarioRepository.save(usuario);
        }
    }

    //Búsquedas
    public List<Evento> buscarPorCategoria(String categoriaId) {
        return eventoRepository.findByCategoriaId(categoriaId);
    }

    public Page<EventoBusquedaDTO> buscarPorTitulo(String titulo, Pageable pageable) {
        Page<Evento> page = eventoRepository.findByTituloContainingIgnoreCase(titulo, pageable);
        List<EventoBusquedaDTO> dtoList = page.getContent().stream().map(evento -> {
            resolverReferencias(evento);
            String nombreCategoria = evento.getCategoria() != null ? evento.getCategoria().getNombre() : null;
            return new EventoBusquedaDTO(evento.getId(), evento.getTitulo(), nombreCategoria);
        }).toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    public long contarEventosPorCategoria(String categoriaId) {
        return eventoRepository.countByCategoriaId(categoriaId);
    }

    //DTO helper
    public EventoDTO convertirADTO(Evento evento) {
        resolverReferencias(evento);
        EventoDTO dto = new EventoDTO();
        dto.setId(evento.getId());
        dto.setTitulo(evento.getTitulo());
        dto.setDescripcion(evento.getDescripcion());
        dto.setLugar(evento.getLugar());
        dto.setFoto(evento.getFoto());
        dto.setFecha(evento.getFecha());
        dto.setHora(evento.getHora());
        if (evento.getCategoria() != null) {
            dto.setCategoria(new EventoDTO.Categoria(evento.getCategoria().getId(), evento.getCategoria().getNombre()));
        }
        if (evento.getEstado() != null) {
            dto.setEstado(new EventoDTO.Estado(evento.getEstado().getId(), evento.getEstado().getNombre()));
        }
        if (evento.getOrganizador() != null) {
            dto.setOrganizador(new EventoDTO.Organizador(
                    evento.getOrganizador().getId(),
                    evento.getOrganizador().getNombre(),
                    evento.getOrganizador().getEsVerificado()));
        }
        return dto;
    }

    //Helpers
    private void denormalizarCategoria(Evento evento) {
        if (evento.getCategoria() == null) return;
        String id = evento.getCategoria().getId();
        if (id == null) return;
        if (evento.getCategoria().getNombre() != null && !evento.getCategoria().getNombre().isBlank()) return;
        categoriasRepository.findById(id).ifPresent(cat -> evento.getCategoria().setNombre(cat.getNombre()));
    }

    private void denormalizarEstado(Evento evento) {
        if (evento.getEstado() == null) return;
        String id = evento.getEstado().getId();
        if (id == null) return;
        if (evento.getEstado().getNombre() != null && !evento.getEstado().getNombre().isBlank()) return;
        estadoRepository.findById(id).ifPresent(est -> evento.getEstado().setNombre(est.getNombre()));
    }

    private void verificarPermiso(Evento evento) {
        Usuario usuario = authHelper.usuarioAutenticado();
        boolean esAdmin = usuario.getRol() != null && "ADMINISTRADOR".equals(usuario.getRol().getNombre());
        boolean esOrganizador = evento.getOrganizador() != null
                && evento.getOrganizador().getCorreo() != null
                && evento.getOrganizador().getCorreo().equals(usuario.getCorreo());
        if (!esAdmin && !esOrganizador) throw new BusinessException("No autorizado");
    }
}
