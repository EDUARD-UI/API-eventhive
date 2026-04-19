package com.example.demo.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.EventoBusquedaDTO;
import com.example.demo.dto.EventoDTO;
import com.example.demo.dto.EventoDestacadoDTO;
import com.example.demo.dto.EventoDetalleDTO;
import com.example.demo.dto.NombreEventoDTO;
import com.example.demo.dto.OrganizadorDashboardDTO;
import com.example.demo.dto.PagedResponse;
import com.example.demo.model.Usuario;
import com.example.demo.service.ServiceEvento;
import com.example.demo.service.ServiceLocalidad;
import com.example.demo.service.ServicePromocion;
import com.example.demo.utils.AuthenticatedUserHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eventos")
public class EventosApiController {

    private final ServiceEvento serviceEventos;
    private final ServiceLocalidad serviceLocalidad;
    private final ServicePromocion servicePromocion;
    private final AuthenticatedUserHelper authHelper;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<EventoDTO>>> listarEventos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<EventoDTO> result = serviceEventos.obtenerEventosPaginado(page, size);
        return ResponseEntity.ok(ApiResponse.ok("Eventos obtenidos", result));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<ApiResponse<PagedResponse<EventoDTO>>> obtenerEventosPorCategoria(
            @PathVariable Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<EventoDTO> result = serviceEventos.buscarPorCategoriaPaginado(categoriaId, page, size);
        return ResponseEntity.ok(ApiResponse.ok("Eventos de la categoría", result));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<EventoBusquedaDTO>>> buscarEventos(
            @RequestParam String titulo) {
        List<EventoBusquedaDTO> resultados = serviceEventos.buscarPorTituloParcial(titulo);
        return ResponseEntity.ok(ApiResponse.ok("Resultados de búsqueda", resultados));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventoDetalleDTO>> obtenerEvento(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Evento obtenido", serviceEventos.obtenerEventoDetalleDTO(id)));
    }

    @GetMapping("/destacados")
    public ResponseEntity<ApiResponse<List<EventoDestacadoDTO>>> obtenerEventosDestacados() {
        return ResponseEntity.ok(ApiResponse.ok("Eventos destacados", serviceEventos.obtenerTop3EventosDTO()));
    }

    @GetMapping("/nombres-Eventos")
    public ResponseEntity<ApiResponse<List<NombreEventoDTO>>> nombresEventos() {
        return ResponseEntity.ok(ApiResponse.ok("Nombres de eventos", serviceEventos.obtenerNombresEventos()));
    }

    //endpoints para organizadores
    @GetMapping("/organizador/mis-eventos")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<PagedResponse<EventoDTO>>> obtenerEventosOrganizador(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Usuario usuario = usuarioAutenticado();
        PagedResponse<EventoDTO> result = serviceEventos.obtenerEventosPorOrganizadorPaginado(usuario.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.ok("Eventos del organizador", result));
    }

    @GetMapping("/organizador/nombres-Eventos")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<List<NombreEventoDTO>>> nombresEventosOrganizador() {
        Usuario usuario = usuarioAutenticado();
        return ResponseEntity.ok(ApiResponse.ok("Nombres de eventos",
                serviceEventos.obtenerNombresEventosPorOrganizador(usuario.getId())));
    }

    @GetMapping("/organizador/dashboard")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<OrganizadorDashboardDTO>> dashboardOrganizador() {
        Usuario usuario = usuarioAutenticado();

        OrganizadorDashboardDTO dto = new OrganizadorDashboardDTO();
        dto.setTotalEventos((int) serviceEventos.contarPorOrganizador(usuario.getId()));
        dto.setTotalLocalidades((int) serviceLocalidad.contarPorOrganizador(usuario.getId()));
        dto.setTotalPromociones((int) servicePromocion.contarPorOrganizador(usuario.getId()));

        return ResponseEntity.ok(ApiResponse.ok("Dashboard cargado", dto));
    }

    //crud
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ORGANIZADOR','ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> crearEvento(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String lugar,
            @RequestParam String fecha,
            @RequestParam String hora,
            @RequestParam Long categoriaId,
            @RequestParam Long estadoId,
            @RequestParam(required = false) MultipartFile foto) throws IOException {

        serviceEventos.crearEvento(
                titulo, descripcion, lugar,
                LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalTime.parse(hora, DateTimeFormatter.ISO_LOCAL_TIME),
                categoriaId, estadoId, foto, usuarioAutenticado());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Evento creado exitosamente"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ORGANIZADOR','ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> actualizarEvento(
            @PathVariable Long id,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String lugar,
            @RequestParam String fecha,
            @RequestParam String hora,
            @RequestParam Long categoriaId,
            @RequestParam Long estadoId,
            @RequestParam(required = false) MultipartFile foto) throws IOException {

        serviceEventos.actualizarEvento(
                id, titulo, descripcion, lugar,
                LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE),
                LocalTime.parse(hora, DateTimeFormatter.ISO_LOCAL_TIME),
                categoriaId, estadoId, foto, usuarioAutenticado());

        return ResponseEntity.ok(ApiResponse.ok("Evento actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZADOR','ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminarEvento(@PathVariable Long id) {
        Usuario usuario = usuarioAutenticado();
        boolean tieneLocalidades = serviceLocalidad.tieneLocalidades(id);
        serviceEventos.eliminarEvento(id, usuario, tieneLocalidades);
        return ResponseEntity.ok(ApiResponse.ok("Evento eliminado exitosamente"));
    }

    // MÉTODO AUXILIAR
    private Usuario usuarioAutenticado() {
        return authHelper.usuarioAutenticado();
    }
}
