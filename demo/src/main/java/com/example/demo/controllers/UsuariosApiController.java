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
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Usuario;
import com.example.demo.service.ServiceUsuario;
import com.example.demo.utils.AuthenticatedUserHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuariosApiController {

    private final ServiceUsuario serviceUsuario;
    private final AuthenticatedUserHelper authHelper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Usuario>>> listarUsuarios() {
        return ResponseEntity.ok(ApiResponse.ok("Usuarios obtenidos", serviceUsuario.obtenerTodosLosUsuarios()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> obtenerUsuario(@PathVariable Long id) {
        Usuario usuario = serviceUsuario.obtenerUsuarioPorId(id);
        if (usuario == null) throw new ResourceNotFoundException("Usuario no encontrado");
        return ResponseEntity.ok(ApiResponse.ok("Usuario obtenido", usuario));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> crearUsuario(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String clave,
            @RequestParam Long rol,
            @RequestParam Long estado) {

        serviceUsuario.crearUsuario(nombre, apellido, correo, telefono, clave, rol, estado);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Usuario creado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> actualizarUsuario(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam(required = false) String clave,
            @RequestParam Long rol,
            @RequestParam Long estado) {

        serviceUsuario.actualizarUsuario(id, nombre, apellido, correo, telefono, clave, rol, estado);
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado exitosamente"));
    }

    @GetMapping("/perfil")
    public ResponseEntity<ApiResponse<Usuario>> obtenerPerfil() {
        Usuario usuario = authHelper.usuarioAutenticado();
        return ResponseEntity.ok(ApiResponse.ok("Perfil obtenido", usuario));
    }

    @PutMapping("/perfil")
    public ResponseEntity<ApiResponse<Usuario>> actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String clave) {

        Usuario usuarioEnSesion = authHelper.usuarioAutenticado();
        Usuario usuarioActualizado = serviceUsuario.actualizarPerfil(usuarioEnSesion, nombre, correo, telefono, clave);
        return ResponseEntity.ok(ApiResponse.ok("Perfil actualizado exitosamente", usuarioActualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminarUsuario(@PathVariable Long id) {
        serviceUsuario.eliminarUsuario(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado exitosamente"));
    }
}