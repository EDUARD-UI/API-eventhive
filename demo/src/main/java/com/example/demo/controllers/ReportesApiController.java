package com.example.demo.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Usuario;
import com.example.demo.security.SecurityController;
import com.example.demo.service.ServiceReportes;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reportes")
public class ReportesApiController {

    private final ServiceReportes serviceReportes;
    private final SecurityController securityController;

    @GetMapping("/eventos-por-categoria")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> eventosPorCategoria() {
        Usuario usuario = securityController.usuarioAutenticado();
        Map<String, Long> data = serviceReportes.contarEventosPorCategoria(usuario.getId());
        return ResponseEntity.ok(ApiResponse.ok("Reporte obtenido",
                Map.of("labels", data.keySet(), "data", data.values())));
    }
}