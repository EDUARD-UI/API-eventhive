package com.example.demo.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.example.demo.dto.UsuarioSesionDTO;
import com.example.demo.model.Usuario;
import com.example.demo.service.ServiceUsuario;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuariosApiController {

    private final ServiceUsuario serviceUsuario;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> obtenerUsuario(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario obtenido",
                serviceUsuario.obtenerUsuarioPorId(id)));
    }

    @GetMapping("/{id}/sesion")
    public ResponseEntity<ApiResponse<UsuarioSesionDTO>> obtenerUsuarioSesion(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Datos de sesión obtenidos",
                serviceUsuario.obtenerSesionDTO(id)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Page<Usuario>>> listarUsuarios(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Usuarios obtenidos",
                serviceUsuario.obtenerTodosUsuarios(pageable)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> actualizarUsuario(
            @PathVariable String id,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String telefono) {

        serviceUsuario.actualizarUsuario(id, nombre, apellido, telefono);
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado exitosamente"));
    }

    @PostMapping("/{id}/cambiar-contrasena")
    public ResponseEntity<ApiResponse<Void>> cambiarContrasena(
            @PathVariable String id,
            @RequestParam String claveAnterior,
            @RequestParam String claveNueva) {

        serviceUsuario.cambiarContrasena(id, claveAnterior, claveNueva);
        return ResponseEntity.ok(ApiResponse.ok("Contraseña cambiada exitosamente"));
    }

    @PostMapping("/{id}/asignar-rol")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> asignarRol(
            @PathVariable String id,
            @RequestParam String rolId) {

        serviceUsuario.asignarRol(id, rolId);
        return ResponseEntity.ok(ApiResponse.ok("Rol asignado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminarUsuario(@PathVariable String id) {
        serviceUsuario.eliminarUsuario(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado exitosamente"));
    }
}
