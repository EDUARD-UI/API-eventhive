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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ValoracionDTO;
import com.example.demo.service.ServiceValoracion;
import com.example.demo.utils.AuthenticatedUserHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/valoraciones")
public class ValoracionesApiController {

    private final ServiceValoracion serviceValoracion;
    private final AuthenticatedUserHelper authHelper;

    @GetMapping("/usuario")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<List<ValoracionDTO>>> valoracionesDelUsuario() {
        return ResponseEntity.ok(ApiResponse.ok("Valoraciones obtenidas",
                serviceValoracion.obtenerValoracionesDTOPorUsuario(authHelper.usuarioAutenticado().getId())));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<ApiResponse<List<ValoracionDTO>>> valoracionesPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(ApiResponse.ok("Valoraciones del evento",
                serviceValoracion.obtenerValoracionesDTOPorEvento(eventoId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<Void>> crearValoracion(
            @RequestParam Long eventoId,
            @RequestParam String comentario,
            @RequestParam long calificacion) {

        serviceValoracion.crearValoracion(authHelper.usuarioAutenticado(), eventoId, comentario, calificacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Valoración creada exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<Void>> actualizarValoracion(
            @PathVariable Long id,
            @RequestParam String comentario,
            @RequestParam long calificacion) {

        serviceValoracion.actualizarValoracion(id, authHelper.usuarioAutenticado(), comentario, calificacion);
        return ResponseEntity.ok(ApiResponse.ok("Valoración actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<Void>> eliminarValoracion(@PathVariable Long id) {
        serviceValoracion.eliminarValoracion(id, authHelper.usuarioAutenticado());
        return ResponseEntity.ok(ApiResponse.ok("Valoración eliminada exitosamente"));
    }
}