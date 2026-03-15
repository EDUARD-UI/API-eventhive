package com.example.demo.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.example.demo.dto.EventoDestacadoDTO;
import com.example.demo.dto.EventoDetalleDTO;
import com.example.demo.dto.NombreEventoDTO;
import com.example.demo.dto.OrganizadorDashboardDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Categoria;
import com.example.demo.model.Estado;
import com.example.demo.model.Evento;
import com.example.demo.model.Usuario;
import com.example.demo.service.ServiceCategoria;
import com.example.demo.service.ServiceEstado;
import com.example.demo.service.ServiceEvento;
import com.example.demo.service.ServiceLocalidad;
import com.example.demo.service.ServicePromocion;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eventos")
public class EventosApiController {

    private final ServiceEvento serviceEventos;
    private final ServiceLocalidad serviceLocalidad;
    private final ServiceCategoria serviceCategoria;
    private final ServiceEstado serviceEstado;
    private final ServicePromocion servicePromocion;

    @Value("${upload.path.eventos:uploads/eventos}")
    private String uploadPath;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Evento>>> listarEventos() {
        return ResponseEntity.ok(ApiResponse.ok("Eventos obtenidos", serviceEventos.todosLosEventos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventoDetalleDTO>> obtenerEvento(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Evento obtenido", serviceEventos.obtenerEventoDetalleDTO(id)));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<EventoBusquedaDTO>>> buscarEventos(@RequestParam String titulo) {
        return ResponseEntity.ok(ApiResponse.ok("Resultados", serviceEventos.buscarPorTituloParcialDTO(titulo)));
    }

    @GetMapping("/destacados")
    public ResponseEntity<ApiResponse<List<EventoDestacadoDTO>>> obtenerEventosDestacados() {
        return ResponseEntity.ok(ApiResponse.ok("Eventos destacados", serviceEventos.obtenerTop3EventosDTO()));
    }

    @GetMapping("/organizador/estadisticas")
    public ResponseEntity<ApiResponse<OrganizadorDashboardDTO>> dashboardOrganizador(HttpSession session) {
        Usuario usuario = GlobalController.rolRequerido(session, "organizador");

        int totalEventos = (int) serviceEventos.contarPorOrganizador(usuario.getId());
        int totalLocalidades = (int) serviceLocalidad.contarPorOrganizador(usuario.getId());
        int totalPromociones = (int) servicePromocion.contarPorOrganizador(usuario.getId());

        OrganizadorDashboardDTO dto = new OrganizadorDashboardDTO();
        dto.setTotalEventos(totalEventos);
        dto.setTotalLocalidades(totalLocalidades);
        dto.setTotalPromociones(totalPromociones);

        return ResponseEntity.ok(ApiResponse.ok("Dashboard cargado", dto));
    }

    // id + titulo de eventos relacionados a organizador logeado
    @GetMapping("/organizador/nombres-Eventos")
    public ResponseEntity<ApiResponse<List<NombreEventoDTO>>> nombresEventosOrganizador(HttpSession session) {
        Usuario usuario = GlobalController.rolRequerido(session, "organizador");
        List<NombreEventoDTO> nombres = serviceEventos.obtenerNombresEventosPorOrganizador(usuario.getId());
        return ResponseEntity.ok(ApiResponse.ok("Nombres de eventos", nombres));
    }

    @GetMapping("/nombres-Eventos")
    public ResponseEntity<ApiResponse<List<NombreEventoDTO>>> nombresEventos() {
        List<NombreEventoDTO> nombres = serviceEventos.obtenerNombresEventos();
        return ResponseEntity.ok(ApiResponse.ok("Nombres de eventos", nombres));
    }

    // operaciones Crud para eventos
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> crearEvento(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String lugar,
            @RequestParam String fecha,
            @RequestParam String hora,
            @RequestParam Long categoriaId,
            @RequestParam Long estadoId,
            @RequestParam(required = false) MultipartFile foto,
            HttpSession session) throws IOException {

        Usuario usuarioLogueado = GlobalController.rolRequerido(session, "organizador", "administrador");

        Categoria categoria = serviceCategoria.obtenerCategoriaPorId(categoriaId);
        Estado estado = serviceEstado.obtenerEstadoPorId(estadoId);
        if (estado == null) throw new ResourceNotFoundException("Estado no encontrado");

        Evento nuevoEvento = new Evento();
        nuevoEvento.setTitulo(titulo);
        nuevoEvento.setDescripcion(descripcion);
        nuevoEvento.setLugar(lugar);
        nuevoEvento.setFecha(LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE));
        nuevoEvento.setHora(LocalTime.parse(hora, DateTimeFormatter.ISO_LOCAL_TIME));
        nuevoEvento.setCategoria(categoria);
        nuevoEvento.setEstado(estado);
        nuevoEvento.setUsuario(usuarioLogueado);

        if (foto != null && !foto.isEmpty()) {
            validarFoto(foto);
            nuevoEvento.setFoto(guardarFoto(foto));
        }

        serviceEventos.crearEvento(nuevoEvento);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Evento creado exitosamente"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> actualizarEvento(
            @PathVariable Long id,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String lugar,
            @RequestParam String fecha,
            @RequestParam String hora,
            @RequestParam Long categoriaId,
            @RequestParam Long estadoId,
            @RequestParam(required = false) MultipartFile foto,
            HttpSession session) throws IOException {

        Usuario usuario = GlobalController.rolRequerido(session, "organizador", "administrador");
        Evento ev = serviceEventos.obtenerEventoPorId(id);

        // solo el organizador o admin puede editar
        if (!ev.getUsuario().getId().equals(usuario.getId())
                && !usuario.getRol().getNombre().equalsIgnoreCase("administrador")) {
            throw new BusinessException("No tiene permisos para editar este evento");
        }

        Categoria categoria = serviceCategoria.obtenerCategoriaPorId(categoriaId);
        Estado estado = serviceEstado.obtenerEstadoPorId(estadoId);
        if (estado == null) throw new ResourceNotFoundException("Estado no encontrado");

        ev.setTitulo(titulo);
        ev.setDescripcion(descripcion);
        ev.setLugar(lugar);
        ev.setFecha(LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE));
        ev.setHora(LocalTime.parse(hora, DateTimeFormatter.ISO_LOCAL_TIME));
        ev.setCategoria(categoria);
        ev.setEstado(estado);

        if (foto != null && !foto.isEmpty()) {
            validarFoto(foto);
            eliminarFoto(ev.getFoto());
            ev.setFoto(guardarFoto(foto));
        }

        serviceEventos.actualizarEvento(ev);
        return ResponseEntity.ok(ApiResponse.ok("Evento actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarEvento(@PathVariable Long id, HttpSession session) {
        Usuario usuario = GlobalController.rolRequerido(session, "organizador", "administrador");
        Evento ev = serviceEventos.obtenerEventoPorId(id);

        if (!ev.getUsuario().getId().equals(usuario.getId())
                && !usuario.getRol().getNombre().equalsIgnoreCase("administrador")) {
            throw new BusinessException("No tiene permisos para eliminar este evento");
        }

        if (!serviceLocalidad.obtenerLocalidadesPorEvento(id).isEmpty())
            throw new BusinessException("No se puede eliminar el evento porque tiene localidades asociadas");

        eliminarFoto(ev.getFoto());
        serviceEventos.eliminarEvento(id);
        return ResponseEntity.ok(ApiResponse.ok("Evento eliminado exitosamente"));
    }

    //funciones de apoyo
    private void validarFoto(MultipartFile foto) {
        if (foto.getSize() > 5 * 1024 * 1024)
            throw new BusinessException("La foto no puede superar los 5MB");
        String ct = foto.getContentType();
        if (ct == null || !ct.startsWith("image/"))
            throw new BusinessException("Solo se permiten archivos de imagen");
    }

    private String guardarFoto(MultipartFile foto) throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
        String ext = "";
        String original = foto.getOriginalFilename();
        if (original != null && original.contains("."))
            ext = original.substring(original.lastIndexOf("."));
        String fileName = UUID.randomUUID() + ext;
        Files.copy(foto.getInputStream(), uploadDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private void eliminarFoto(String nombreFoto) {
        if (nombreFoto != null && !nombreFoto.isBlank()) {
            try { Files.deleteIfExists(Paths.get(uploadPath).resolve(nombreFoto)); }
            catch (IOException e) { System.err.println("Error al eliminar foto: " + e.getMessage()); }
        }
    }
}