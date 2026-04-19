package com.example.demo.controllers;

import java.math.BigDecimal;

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
import com.example.demo.dto.PagedResponse;
import com.example.demo.model.Localidad;
import com.example.demo.model.Usuario;
import com.example.demo.service.ServiceLocalidad;
import com.example.demo.utils.AuthenticatedUserHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/localidades")
public class LocalidadesApiController {

    private final ServiceLocalidad serviceLocalidad;
    private final AuthenticatedUserHelper authHelper;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Localidad>>> listarLocalidades(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<Localidad> result = serviceLocalidad.obtenerLocalidadesPaginado(page, size);
        return ResponseEntity.ok(ApiResponse.ok("Localidades obtenidas", result));
    }

    @GetMapping("/organizador")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<PagedResponse<Localidad>>> listarPorOrganizador(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Usuario u = authHelper.usuarioAutenticado();
        PagedResponse<Localidad> result = serviceLocalidad.obtenerPorOrganizadorPaginado(u.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.ok("Localidades obtenidas", result));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<ApiResponse<PagedResponse<Localidad>>> listarPorEvento(
            @PathVariable String eventoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<Localidad> result = serviceLocalidad.obtenerPorEventoPaginado(eventoId, page, size);
        return ResponseEntity.ok(ApiResponse.ok("Localidades del evento", result));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANIZADOR','ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> crearLocalidad(
            @RequestParam String nombre,
            @RequestParam BigDecimal precio,
            @RequestParam Integer capacidad,
            @RequestParam Integer disponibles,
            @RequestParam String eventoId) {

        serviceLocalidad.crearLocalidad(nombre, precio, capacidad, disponibles, eventoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Localidad creada exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZADOR','ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> actualizarLocalidad(
            @PathVariable String id,
            @RequestParam String nombre,
            @RequestParam BigDecimal precio,
            @RequestParam Integer capacidad,
            @RequestParam Integer disponibles,
            @RequestParam String eventoId) {

        serviceLocalidad.actualizarLocalidad(id, nombre, precio, capacidad, disponibles, eventoId);
        return ResponseEntity.ok(ApiResponse.ok("Localidad actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZADOR','ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminarLocalidad(@PathVariable String id) {
        serviceLocalidad.eliminarLocalidad(id);
        return ResponseEntity.ok(ApiResponse.ok("Localidad eliminada exitosamente"));
    }
}
