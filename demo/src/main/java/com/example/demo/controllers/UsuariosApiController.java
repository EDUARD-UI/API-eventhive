package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.service.ServiceUsuario;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuariosApiController {

    private final ServiceUsuario usuarioService;

    @GetMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UsuarioDTO>> perfil() {
        return ResponseEntity.ok(ApiResponse.ok("Perfil obtenido", 
            usuarioService.obtenerPerfil()));
    }

    @PutMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UsuarioDTO>> actualizarPerfil(@RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Perfil actualizado",
            usuarioService.actualizarPerfil(dto)));
    }

    @GetMapping("/eventos-deseados")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> eventosDeseados() {
        return ResponseEntity.ok(ApiResponse.ok("Eventos deseados",
            usuarioService.listarEventosDeseados()));
    }
}