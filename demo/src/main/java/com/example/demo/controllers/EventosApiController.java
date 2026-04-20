package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<List<Evento>>> listarEventos() {
        return ResponseEntity.ok(ApiResponse.ok(eventoService.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Evento>> obtenerEvento(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(eventoService.obtenerPorId(id)));
    }

    @GetMapping("/{id}/localidades")
    public ResponseEntity<ApiResponse<List<Localidad>>> listarLocalidades(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(eventoService.obtenerLocalidades(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Evento>> crearEvento(@RequestBody Evento evento) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(eventoService.crearEvento(evento)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Evento>> actualizarEvento(@PathVariable String id, @RequestBody Evento evento) {
        return ResponseEntity.ok(ApiResponse.ok(eventoService.actualizarEvento(id, evento)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminarEvento(@PathVariable String id) {
        eventoService.eliminarEvento(id);
        return ResponseEntity.ok(ApiResponse.ok("Evento eliminado"));
    }

    @PostMapping("/{eventoId}/localidades")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Evento>> agregarLocalidad(
            @PathVariable String eventoId, 
            @RequestBody Localidad localidad) {
        return ResponseEntity.ok(ApiResponse.ok(eventoService.agregarLocalidad(eventoId, localidad)));
    }

    @PutMapping("/{eventoId}/localidades/{localidadIndex}")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Evento>> actualizarLocalidad(
            @PathVariable String eventoId,
            @PathVariable int localidadIndex,
            @RequestBody Localidad localidad) {
        return ResponseEntity.ok(ApiResponse.ok(eventoService.actualizarLocalidad(eventoId, localidadIndex, localidad)));
    }

    @DeleteMapping("/{eventoId}/localidades/{localidadIndex}")
    @PreAuthorize("hasRole('ORGANIZADOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Evento>> eliminarLocalidad(
            @PathVariable String eventoId,
            @PathVariable int localidadIndex) {
        return ResponseEntity.ok(ApiResponse.ok(eventoService.eliminarLocalidad(eventoId, localidadIndex)));
    }

    @PostMapping("/{eventoId}/deseados")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> agregarEventoDeseado(@PathVariable String eventoId) {
        eventoService.agregarEventoDeseado(eventoId);
        return ResponseEntity.ok(ApiResponse.ok("Evento agregado a favoritos"));
    }

    @DeleteMapping("/{eventoId}/deseados")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> eliminarEventoDeseado(@PathVariable String eventoId) {
        eventoService.eliminarEventoDeseado(eventoId);
        return ResponseEntity.ok(ApiResponse.ok("Evento eliminado de favoritos"));
    }
}