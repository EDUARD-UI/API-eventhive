package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.EventoBusquedaDTO;
import com.example.demo.dto.EventoDTO;
import com.example.demo.dto.EventoDestacadoDTO;
import com.example.demo.dto.EventoDetalleDTO;
import com.example.demo.dto.NombreEventoDTO;
import com.example.demo.dto.PagedResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Categoria;
import com.example.demo.model.Estado;
import com.example.demo.model.Evento;
import com.example.demo.model.Localidad;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CategoriasRepository;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.LocalidadRepository;
import com.example.demo.utils.Utilidades;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceEvento {

    private final EventoRepository eventoRepository;
    private final LocalidadRepository localidadRepository;
    private final CategoriasRepository categoriasRepository;
    private final ServiceEstado serviceEstado;

    @Value("${upload.path.eventos:uploads/eventos}")
    private String uploadPath;

    private Categoria resolverCategoria(Long categoriaId) {
        return categoriasRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + categoriaId));
    }

    public List<Evento> todosLosEventos() {
        return eventoRepository.findAll();
    }

    public long contarPorOrganizador(Long id) {
        return eventoRepository.countByUsuarioId(id);
    }

    public long contarEventosPorCategoria(Long catId) {
        return eventoRepository.countByCategoriaId(catId);
    }

    public Evento crearEvento(String titulo, String descripcion, String lugar,
            LocalDate fecha, LocalTime hora,
            Long categoriaId, Long estadoId,
            MultipartFile foto, Usuario organizador) throws IOException {

        Categoria categoria = resolverCategoria(categoriaId);
        Estado estado = serviceEstado.obtenerEstadoPorId(estadoId);

        Evento evento = new Evento();
        evento.setTitulo(titulo);
        evento.setDescripcion(descripcion);
        evento.setLugar(lugar);
        evento.setFecha(fecha);
        evento.setHora(hora);
        evento.setCategoria(categoria);
        evento.setEstado(estado);
        evento.setUsuario(organizador);

        if (foto != null && !foto.isEmpty()) {
            Utilidades.validarFoto(foto);
            evento.setFoto(Utilidades.guardarFoto(foto, uploadPath));
        }

        return eventoRepository.save(evento);
    }

    public Evento actualizarEvento(Long id, String titulo, String descripcion, String lugar,
            LocalDate fecha, LocalTime hora,
            Long categoriaId, Long estadoId,
            MultipartFile foto, Usuario solicitante) throws IOException {

        Evento ev = obtenerEventoPorId(id);

        boolean esAdmin = solicitante.getRol().getNombre().equalsIgnoreCase("administrador");
        if (!ev.getUsuario().getId().equals(solicitante.getId()) && !esAdmin) {
            throw new BusinessException("No tiene permisos para editar este evento");
        }

        Categoria categoria = resolverCategoria(categoriaId);
        Estado estado = serviceEstado.obtenerEstadoPorId(estadoId);

        ev.setTitulo(titulo);
        ev.setDescripcion(descripcion);
        ev.setLugar(lugar);
        ev.setFecha(fecha);
        ev.setHora(hora);
        ev.setCategoria(categoria);
        ev.setEstado(estado);

        if (foto != null && !foto.isEmpty()) {
            Utilidades.validarFoto(foto);
            Utilidades.eliminarFoto(ev.getFoto(), uploadPath);
            ev.setFoto(Utilidades.guardarFoto(foto, uploadPath));
        }

        return eventoRepository.save(ev);
    }

    public void eliminarEvento(Long id, Usuario solicitante, boolean tieneLocalidades) {
        Evento ev = obtenerEventoPorId(id);

        // Validar que sea el dueño o administrador
        boolean esAdmin = solicitante.getRol().getNombre().equalsIgnoreCase("administrador");
        if (!ev.getUsuario().getId().equals(solicitante.getId()) && !esAdmin) {
            throw new BusinessException("No tiene permisos para eliminar este evento");
        }

        // Validar que no tenga localidades asignadas
        if (tieneLocalidades) {
            throw new BusinessException("No se puede eliminar el evento porque tiene localidades asociadas");
        }

        Utilidades.eliminarFoto(ev.getFoto(), uploadPath);
        eventoRepository.deleteById(id);
    }

    // Consultas y DTOs
    public Evento obtenerEventoPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));
    }

    // Métodos paginados
    public PagedResponse<EventoDTO> obtenerEventosPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Evento> pageResult = eventoRepository.findAll(pageable);

        List<EventoDTO> dtos = pageResult.getContent()
                .stream()
                .map(this::toEventoDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                dtos,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    public PagedResponse<EventoDTO> buscarPorCategoriaPaginado(Long categoriaId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Evento> pageResult = eventoRepository.findByCategoriaId(categoriaId, pageable);

        List<EventoDTO> dtos = pageResult.getContent()
                .stream()
                .map(this::toEventoDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                dtos,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    public PagedResponse<EventoDTO> obtenerEventosPorOrganizadorPaginado(Long organizadorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Evento> pageResult = eventoRepository.findByUsuarioId(organizadorId, pageable);

        List<EventoDTO> dtos = pageResult.getContent()
                .stream()
                .map(e -> {
                    EventoDTO dto = toEventoDTO(e);
                    dto.setHora(e.getHora());
                    if (e.getCategoria() != null) {
                        dto.setCategoria(new EventoDTO.Categoria(e.getCategoria().getId(), e.getCategoria().getNombre()));
                    }
                    if (e.getEstado() != null) {
                        dto.setEstado(new EventoDTO.Estado(e.getEstado().getId(), e.getEstado().getNombre()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(
                dtos,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    // Métodos sin paginación (retornan pocas filas)
    public List<EventoBusquedaDTO> buscarPorTituloParcialDTO(String titulo) {
        return eventoRepository.findByTituloContainingIgnoreCase(titulo).stream()
                .map(e -> new EventoBusquedaDTO(
                e.getId(),
                e.getTitulo(),
                e.getCategoria() != null ? e.getCategoria().getNombre() : "General"))
                .collect(Collectors.toList());
    }

    public List<EventoDTO> buscarPorCategoriaDTO(Long categoriaId) {
        return eventoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::toEventoDTO)
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

    public EventoDetalleDTO obtenerEventoDetalleDTO(Long id) {
        Evento e = obtenerEventoPorId(id);

        EventoDetalleDTO dto = new EventoDetalleDTO();
        dto.setId(e.getId());
        dto.setTitulo(e.getTitulo());
        dto.setDescripcion(e.getDescripcion());
        dto.setFoto(e.getFoto());
        dto.setFecha(e.getFecha());
        dto.setHora(e.getHora());
        dto.setLugar(e.getLugar());
        if (e.getCategoria() != null) {
            dto.setCategoriaNombre(e.getCategoria().getNombre());
        }
        if (e.getEstado() != null) {
            dto.setEstadoNombre(e.getEstado().getNombre());
        }

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

    public List<EventoDTO> obtenerEventosPorOrganizador(Long organizadorId) {
        return eventoRepository.findByUsuarioId(organizadorId).stream()
                .map(e -> {
                    EventoDTO dto = toEventoDTO(e);
                    dto.setHora(e.getHora());
                    if (e.getCategoria() != null) {
                        dto.setCategoria(new EventoDTO.Categoria(e.getCategoria().getId(), e.getCategoria().getNombre()));
                    }
                    if (e.getEstado() != null) {
                        dto.setEstado(new EventoDTO.Estado(e.getEstado().getId(), e.getEstado().getNombre()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<NombreEventoDTO> obtenerNombresEventos() {
        return eventoRepository.findAll().stream()
                .map(e -> new NombreEventoDTO(e.getId(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<NombreEventoDTO> obtenerNombresEventosPorOrganizador(Long organizadorId) {
        return eventoRepository.findNombresByOrganizadorId(organizadorId);
    }

    private EventoDTO toEventoDTO(Evento e) {
        EventoDTO dto = new EventoDTO();
        dto.setId(e.getId());
        dto.setTitulo(e.getTitulo());
        dto.setDescripcion(e.getDescripcion());
        dto.setLugar(e.getLugar());
        dto.setFoto(e.getFoto());
        dto.setFecha(e.getFecha());
        dto.setHora(e.getHora());

        //Agregar categoría
        if (e.getCategoria() != null) {
            dto.setCategoria(new EventoDTO.Categoria(e.getCategoria().getId(), e.getCategoria().getNombre()));
        }

        //Agregar estado
        if (e.getEstado() != null) {
            dto.setEstado(new EventoDTO.Estado(e.getEstado().getId(), e.getEstado().getNombre()));
        }

        return dto;
    }
}
