package com.example.demo.controllers;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Evento;
import com.example.demo.model.Localidad;
import com.example.demo.service.ServiceEvento;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventosApiController {

    private final ServiceEvento eventoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Evento>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Eventos obtenidos", 
            eventoService.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Evento>> obtener(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Evento obtenido", 
            eventoService.obtenerPorId(id)));
    }

    @GetMapping("/{id}/localidades")
    public ResponseEntity<ApiResponse<List<Localidad>>> localidades(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Localidades obtenidas",
            eventoService.obtenerLocalidades(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Evento>> crear(@RequestBody Evento evento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Evento creado",
            eventoService.crearEvento(evento)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Evento>> actualizar(@PathVariable String id, @RequestBody Evento evento) {
        return ResponseEntity.ok(ApiResponse.ok("Evento actualizado",
            eventoService.actualizarEvento(id, evento)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String id) {
        eventoService.eliminarEvento(id);
        return ResponseEntity.ok(ApiResponse.ok("Evento eliminado"));
    }

    @PostMapping("/{eventoId}/localidades")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Evento>> agregarLocalidad(
            @PathVariable String eventoId, @RequestBody Localidad localidad) {
        return ResponseEntity.ok(ApiResponse.ok("Localidad agregada",
            eventoService.agregarLocalidad(eventoId, localidad)));
    }

    @PutMapping("/{eventoId}/localidades/{localidadIndex}")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Evento>> actualizarLocalidad(
            @PathVariable String eventoId,
            @PathVariable int localidadIndex,
            @RequestBody Localidad localidad) {
        return ResponseEntity.ok(ApiResponse.ok("Localidad actualizada",
            eventoService.actualizarLocalidad(eventoId, localidadIndex, localidad)));
    }

    @DeleteMapping("/{eventoId}/localidades/{localidadIndex}")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Evento>> eliminarLocalidad(
            @PathVariable String eventoId,
            @PathVariable int localidadIndex) {
        return ResponseEntity.ok(ApiResponse.ok("Localidad eliminada",
            eventoService.eliminarLocalidad(eventoId, localidadIndex)));
    }

    @PostMapping("/{eventoId}/deseados")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> agregarDeseado(@PathVariable String eventoId) {
        eventoService.agregarEventoDeseado(eventoId);
        return ResponseEntity.ok(ApiResponse.ok("Evento agregado a favoritos"));
    }

    @DeleteMapping("/{eventoId}/deseados")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> eliminarDeseado(@PathVariable String eventoId) {
        eventoService.eliminarEventoDeseado(eventoId);
        return ResponseEntity.ok(ApiResponse.ok("Evento eliminado de favoritos"));
    }
}