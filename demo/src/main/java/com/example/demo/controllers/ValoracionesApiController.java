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
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Evento;
import com.example.demo.model.Usuario;
import com.example.demo.model.Valoracion;
import com.example.demo.security.SecurityController;
import com.example.demo.service.ServiceEvento;
import com.example.demo.service.ServiceValoracion;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/valoraciones")
public class ValoracionesApiController {

    private final ServiceValoracion serviceValoracion;
    private final ServiceEvento serviceEvento;
    private final SecurityController securityController;

    @GetMapping("/usuario")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<List<ValoracionDTO>>> valoracionesDelUsuario() {
        return ResponseEntity.ok(ApiResponse.ok("Valoraciones obtenidas",
                serviceValoracion.obtenerValoracionesDTOPorUsuario(usuarioAutenticado().getId())));
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

        if (calificacion < 1 || calificacion > 5)
            throw new BusinessException("La calificación debe estar entre 1 y 5");

        Evento evento = serviceEvento.obtenerEventoPorId(eventoId);

        Valoracion v = new Valoracion();
        v.setCliente(usuarioAutenticado());
        v.setEvento(evento);
        v.setComentario(comentario);
        v.setCalificacion(calificacion);
        serviceValoracion.crear(v);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Valoración creada exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<Void>> actualizarValoracion(
            @PathVariable Long id,
            @RequestParam String comentario,
            @RequestParam long calificacion) {

        if (calificacion < 1 || calificacion > 5)
            throw new BusinessException("La calificación debe estar entre 1 y 5");

        Valoracion v = obtenerValoracionVerificada(id, usuarioAutenticado());
        v.setComentario(comentario);
        v.setCalificacion(calificacion);
        serviceValoracion.actualizarValoracion(v);

        return ResponseEntity.ok(ApiResponse.ok("Valoración actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<Void>> eliminarValoracion(@PathVariable Long id) {
        obtenerValoracionVerificada(id, usuarioAutenticado());
        serviceValoracion.eliminarValoracion(id);
        return ResponseEntity.ok(ApiResponse.ok("Valoración eliminada exitosamente"));
    }

    private Usuario usuarioAutenticado() {
        return securityController.usuarioAutenticado();
    }

    private Valoracion obtenerValoracionVerificada(Long id, Usuario usuario) {
    Valoracion v = serviceValoracion.obtenerValoracionPorId(id);
    if (v == null) 
        throw new ResourceNotFoundException("Valoración no encontrada");
    if (v.getCliente() == null || !v.getCliente().getId().equals(usuario.getId())) 
        throw new BusinessException("No autorizado");
    return v;
}
}