package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.EventoBusquedaDTO;
import com.example.demo.dto.EventoDTO;
import com.example.demo.dto.EventoDestacadoDTO;
import com.example.demo.dto.EventoDetalleDTO;
import com.example.demo.dto.NombreEventoDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Categoria;
import com.example.demo.model.Estado;
import com.example.demo.model.Evento;
import com.example.demo.model.Localidad;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.LocalidadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceEvento {

    private final EventoRepository eventoRepository;
    private final LocalidadRepository localidadRepository;
    private final ServiceCategoria serviceCategoria;
    private final ServiceEstado serviceEstado;

    @Value("${upload.path.eventos:uploads/eventos}")
    private String uploadPath;

    // --- CRUD con lógica de negocio incluida ---

    public Evento crearEvento(String titulo, String descripcion, String lugar,
                               LocalDate fecha, LocalTime hora,
                               Long categoriaId, Long estadoId,
                               MultipartFile foto, Usuario organizador) throws IOException {

        Categoria categoria = serviceCategoria.obtenerCategoriaPorId(categoriaId);
        Estado estado = serviceEstado.obtenerEstadoPorId(estadoId);
        if (estado == null) throw new ResourceNotFoundException("Estado no encontrado");

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
            validarFoto(foto);
            evento.setFoto(guardarFoto(foto));
        }

        return eventoRepository.save(evento);
    }

    public Evento actualizarEvento(Long id, String titulo, String descripcion, String lugar,
                                    LocalDate fecha, LocalTime hora,
                                    Long categoriaId, Long estadoId,
                                    MultipartFile foto, Usuario solicitante) throws IOException {

        Evento ev = obtenerEventoPorId(id);

        boolean esAdmin = solicitante.getRol().getNombre().equalsIgnoreCase("administrador");
        if (!ev.getUsuario().getId().equals(solicitante.getId()) && !esAdmin)
            throw new BusinessException("No tiene permisos para editar este evento");

        Categoria categoria = serviceCategoria.obtenerCategoriaPorId(categoriaId);
        Estado estado = serviceEstado.obtenerEstadoPorId(estadoId);
        if (estado == null) throw new ResourceNotFoundException("Estado no encontrado");

        ev.setTitulo(titulo);
        ev.setDescripcion(descripcion);
        ev.setLugar(lugar);
        ev.setFecha(fecha);
        ev.setHora(hora);
        ev.setCategoria(categoria);
        ev.setEstado(estado);

        if (foto != null && !foto.isEmpty()) {
            validarFoto(foto);
            eliminarFoto(ev.getFoto());
            ev.setFoto(guardarFoto(foto));
        }

        return eventoRepository.save(ev);
    }

    public void eliminarEvento(Long id, Usuario solicitante, boolean tieneLocalidades) {
        Evento ev = obtenerEventoPorId(id);

        boolean esAdmin = solicitante.getRol().getNombre().equalsIgnoreCase("administrador");
        if (!ev.getUsuario().getId().equals(solicitante.getId()) && !esAdmin)
            throw new BusinessException("No tiene permisos para eliminar este evento");

        if (tieneLocalidades)
            throw new BusinessException("No se puede eliminar el evento porque tiene localidades asociadas");

        eliminarFoto(ev.getFoto());
        eventoRepository.deleteById(id);
    }

    // Queries y DTOs

    public List<EventoBusquedaDTO> buscarPorTituloParcialDTO(String titulo) {
        return eventoRepository.findByTituloContainingIgnoreCase(titulo).stream()
                .map(e -> new EventoBusquedaDTO(
                        e.getId(), e.getTitulo(),
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
                    if (e.getCategoria() != null) dto.setCategoriaNombre(e.getCategoria().getNombre());
                    return dto;
                })
                .collect(Collectors.toList());
    }

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

    public List<EventoDTO> obtenerEventosPorOrganizador(Long organizadorId) {
        return eventoRepository.findByUsuarioId(organizadorId).stream()
                .map(e -> {
                    EventoDTO dto = toEventoDTO(e);
                    dto.setHora(e.getHora());
                    if (e.getCategoria() != null)
                        dto.setCategoria(new EventoDTO.Categoria(e.getCategoria().getId(), e.getCategoria().getNombre()));
                    if (e.getEstado() != null)
                        dto.setEstado(new EventoDTO.Estado(e.getEstado().getId(), e.getEstado().getNombre()));
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

    public Evento obtenerEventoPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));
    }

    public List<Evento> todosLosEventos()               { return eventoRepository.findAll(); }
    public long contarPorOrganizador(Long id)           { return eventoRepository.countByUsuarioId(id); }
    public long contarEventosPorCategoria(Long catId)   { return eventoRepository.countByCategoriaId(catId); }

    //metodos de apoyo
    private void validarFoto(MultipartFile foto) {
        if (foto.getSize() > 5 * 1024 * 1024)
            throw new BusinessException("La foto no puede superar los 5MB");
        String ct = foto.getContentType();
        if (ct == null || !ct.startsWith("image/"))
            throw new BusinessException("Solo se permiten archivos de imagen");
    }

    private String guardarFoto(MultipartFile foto) throws IOException {
        Path dir = Paths.get(uploadPath);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        String ext = "";
        String original = foto.getOriginalFilename();
        if (original != null && original.contains("."))
            ext = original.substring(original.lastIndexOf("."));
        String fileName = UUID.randomUUID() + ext;
        Files.copy(foto.getInputStream(), dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private void eliminarFoto(String nombreFoto) {
        if (nombreFoto != null && !nombreFoto.isBlank()) {
            try { Files.deleteIfExists(Paths.get(uploadPath).resolve(nombreFoto)); }
            catch (IOException e) { System.err.println("Error al eliminar foto: " + e.getMessage()); }
        }
    }

    private EventoDTO toEventoDTO(Evento e) {
        EventoDTO dto = new EventoDTO();
        dto.setId(e.getId());
        dto.setTitulo(e.getTitulo());
        dto.setDescripcion(e.getDescripcion());
        dto.setLugar(e.getLugar());
        dto.setFoto(e.getFoto());
        dto.setFecha(e.getFecha());
        return dto;
    }
}