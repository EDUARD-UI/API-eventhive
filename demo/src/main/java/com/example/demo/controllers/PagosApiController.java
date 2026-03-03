package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Usuario;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/pagos")
public class PagosApiController {

    @GetMapping
    public ResponseEntity<ApiResponse<Usuario>> mostrarPago(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuario == null) throw new BusinessException("Usuario no autenticado");

        return ResponseEntity.ok(ApiResponse.ok("Usuario autenticado para pago", usuario));
    }
}