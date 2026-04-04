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
import com.example.demo.model.Rol;
import com.example.demo.service.ServiceRoles;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RolesApiController {

    private final ServiceRoles serviceRoles;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Rol>>> listarRoles() {
        return ResponseEntity.ok(ApiResponse.ok("Roles obtenidos", serviceRoles.obtenerTodosRoles()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> crearRol(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam Long estadoId) {

        serviceRoles.crearRol(nombre, descripcion, estadoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Rol creado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> actualizarRol(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam Long estadoId) {

        serviceRoles.actualizarRol(id, nombre, descripcion, estadoId);
        return ResponseEntity.ok(ApiResponse.ok("Rol actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminarRol(@PathVariable Long id) {
        serviceRoles.eliminarRol(id);
        return ResponseEntity.ok(ApiResponse.ok("Rol eliminado exitosamente"));
    }
}