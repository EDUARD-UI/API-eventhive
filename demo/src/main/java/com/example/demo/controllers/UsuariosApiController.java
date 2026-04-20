package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<UsuarioDTO>> obtenerPerfil() {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.obtenerPerfil()));
    }

    @PutMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UsuarioDTO>> actualizarPerfil(@RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.actualizarPerfil(usuarioDTO)));
    }

    @GetMapping("/eventos-deseados")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> listarEventosDeseados() {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.listarEventosDeseados()));
    }
}