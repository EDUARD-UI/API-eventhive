package com.example.demo.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.EventoBusquedaDTO;
import com.example.demo.dto.EventoDTO;
import com.example.demo.dto.PagedResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Evento;
import com.example.demo.model.Localidad;
import com.example.demo.model.Usuario;
import com.example.demo.service.ServiceEvento;
import com.example.demo.utils.AuthenticatedUserHelper;
import com.example.demo.utils.MongoSerializationHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventosApiController {

    private final ServiceEvento eventoService;
    private final AuthenticatedUserHelper authHelper;

    // ── LISTAR CON FILTRO OPCIONAL POR CATEGORÍA ──────────────────────────────
    // Se añade el parámetro `categoriaId` para que el frontend pueda filtrar
    // sin necesidad de una ruta separada.
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<EventoDTO>>> listar(
            @RequestParam(required = false) String categoriaId,
            Pageable pageable) {
        try {
            Page<Evento> page;
            if (categoriaId != null && !categoriaId.isBlank()) {
                page = eventoService.listarPorCategoria(categoriaId, pageable);
            } else {
                page = eventoService.listarTodos(pageable);
            }
            List<EventoDTO> contenidoDTO = toDTO(page);
            return ResponseEntity.ok(ApiResponse.ok("Eventos obtenidos", buildPaged(page, contenidoDTO)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al listar eventos: " + e.getMessage()));
        }
    }

    @GetMapping("/organizador")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<PagedResponse<EventoDTO>>> listarMisEventos(Pageable pageable) {
        try {
            Usuario organizador = authHelper.usuarioAutenticado();
            Page<Evento> page = eventoService.listarPorOrganizador(organizador.getId(), pageable);
            List<EventoDTO> contenidoDTO = toDTOSimple(page);
            return ResponseEntity.ok(ApiResponse.ok("Eventos obtenidos", buildPaged(page, contenidoDTO)));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al listar eventos: " + e.getMessage()));
        }
    }

    @GetMapping("/organizador/buscar")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<PagedResponse<EventoDTO>>> buscarMisEventos(
            @RequestParam(required = false, name = "titulo") String titulo,
            Pageable pageable) {
        try {
            if (titulo == null || titulo.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Parámetro 'titulo' requerido"));
            }
            Usuario organizador = authHelper.usuarioAutenticado();
            Page<Evento> page = eventoService.buscarPorOrganizadorYTitulo(
                    organizador.getId(), titulo.trim(), pageable);
            List<EventoDTO> contenidoDTO = toDTOSimple(page);
            return ResponseEntity.ok(ApiResponse.ok("Resultados de búsqueda", buildPaged(page, contenidoDTO)));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en búsqueda: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventoDTO>> obtener(@PathVariable String id) {
        try {
            Evento evento = eventoService.obtenerPorId(id);
            MongoSerializationHelper.forzarCargaReferencias(evento);
            if (evento.getFoto() != null && evento.getFoto().trim().isEmpty()) evento.setFoto(null);
            EventoDTO dto = MongoSerializationHelper.eventoADTO(evento);
            return ResponseEntity.ok(ApiResponse.ok("Evento obtenido", dto));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener evento: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/localidades")
    public ResponseEntity<ApiResponse<List<Localidad>>> localidades(@PathVariable String id) {
        try {
            List<Localidad> localidades = eventoService.obtenerLocalidades(id);
            return ResponseEntity.ok(ApiResponse.ok("Localidades obtenidas", localidades));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<EventoDTO>> crear(@RequestBody Evento evento) {
        try {
            Evento eventoCreado = eventoService.crearEvento(evento);
            MongoSerializationHelper.forzarCargaReferencias(eventoCreado);
            EventoDTO dto = MongoSerializationHelper.eventoADTO(eventoCreado);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Evento creado", dto));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear evento: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<EventoDTO>> actualizar(
            @PathVariable String id,
            @RequestBody Evento evento) {
        try {
            Evento eventoActualizado = eventoService.actualizarEvento(id, evento);
            MongoSerializationHelper.forzarCargaReferencias(eventoActualizado);
            EventoDTO dto = MongoSerializationHelper.eventoADTO(eventoActualizado);
            return ResponseEntity.ok(ApiResponse.ok("Evento actualizado", dto));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al actualizar evento: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String id) {
        try {
            eventoService.eliminarEvento(id);
            return ResponseEntity.ok(ApiResponse.ok("Evento eliminado"));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{eventoId}/localidades")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<EventoDTO>> agregarLocalidad(
            @PathVariable String eventoId,
            @RequestBody Localidad localidad) {
        try {
            Evento eventoActualizado = eventoService.agregarLocalidad(eventoId, localidad);
            MongoSerializationHelper.forzarCargaReferencias(eventoActualizado);
            return ResponseEntity.ok(ApiResponse.ok("Localidad agregada",
                    MongoSerializationHelper.eventoADTO(eventoActualizado)));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{eventoId}/localidades/{localidadIndex}")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<EventoDTO>> actualizarLocalidad(
            @PathVariable String eventoId,
            @PathVariable int localidadIndex,
            @RequestBody Localidad localidad) {
        try {
            Evento eventoActualizado = eventoService.actualizarLocalidad(eventoId, localidadIndex, localidad);
            MongoSerializationHelper.forzarCargaReferencias(eventoActualizado);
            return ResponseEntity.ok(ApiResponse.ok("Localidad actualizada",
                    MongoSerializationHelper.eventoADTO(eventoActualizado)));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{eventoId}/localidades/{localidadIndex}")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<EventoDTO>> eliminarLocalidad(
            @PathVariable String eventoId,
            @PathVariable int localidadIndex) {
        try {
            Evento eventoActualizado = eventoService.eliminarLocalidad(eventoId, localidadIndex);
            MongoSerializationHelper.forzarCargaReferencias(eventoActualizado);
            return ResponseEntity.ok(ApiResponse.ok("Localidad eliminada",
                    MongoSerializationHelper.eventoADTO(eventoActualizado)));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{eventoId}/deseados")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> agregarDeseado(@PathVariable String eventoId) {
        try {
            eventoService.agregarEventoDeseado(eventoId);
            return ResponseEntity.ok(ApiResponse.ok("Evento agregado a favoritos"));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{eventoId}/deseados")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> eliminarDeseado(@PathVariable String eventoId) {
        try {
            eventoService.eliminarEventoDeseado(eventoId);
            return ResponseEntity.ok(ApiResponse.ok("Evento eliminado de favoritos"));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<PagedResponse<EventoBusquedaDTO>>> buscar(
            @RequestParam(required = false, name = "titulo") String titulo,
            Pageable pageable) {
        try {
            if (titulo == null || titulo.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Parámetro 'titulo' requerido"));
            }
            Page<EventoBusquedaDTO> page = eventoService.buscarPorTitulo(titulo, pageable);
            PagedResponse<EventoBusquedaDTO> response = new PagedResponse<>(
                    page.getContent(), page.getNumber(), page.getSize(),
                    page.getTotalElements(), page.getTotalPages());
            return ResponseEntity.ok(ApiResponse.ok("Resultados de búsqueda", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en búsqueda: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/buscar")
    public ResponseEntity<ApiResponse<PagedResponse<EventoDTO>>> buscarAdmin(
            @RequestParam(required = false, name = "titulo") String titulo,
            @RequestParam(required = false, name = "categoriaId") String categoriaId,
            @RequestParam(required = false, name = "estadoId") String estadoId,
            Pageable pageable) {
        try {
            Page<Evento> page = eventoService.buscarAdmin(titulo, categoriaId, estadoId, pageable);
            List<EventoDTO> contenidoDTO = toDTO(page);
            return ResponseEntity.ok(ApiResponse.ok("Resultados de búsqueda", buildPaged(page, contenidoDTO)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en búsqueda: " + e.getMessage()));
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private List<EventoDTO> toDTO(Page<Evento> page) {
        return page.getContent().stream()
                .map(evento -> {
                    MongoSerializationHelper.forzarCargaReferencias(evento);
                    EventoDTO dto = MongoSerializationHelper.eventoADTO(evento);
                    if (evento.getFoto() != null && evento.getFoto().trim().isEmpty()) dto.setFoto(null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<EventoDTO> toDTOSimple(Page<Evento> page) {
        return page.getContent().stream()
                .map(evento -> {
                    EventoDTO dto = MongoSerializationHelper.eventoADTO(evento);
                    if (evento.getFoto() != null && evento.getFoto().trim().isEmpty()) dto.setFoto(null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private <T> PagedResponse<T> buildPaged(Page<?> page, List<T> content) {
        return new PagedResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
