package com.example.demo.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Usuario;
import com.example.demo.service.ServiceReportes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reportes")
public class ReportesApiController {

    private final ServiceReportes serviceReportes;

    @GetMapping("/eventos-por-categoria")
    public ResponseEntity<ApiResponse<Map<String, Object>>> eventosPorCategoria(HttpSession session) {
        Usuario usuario = getOrganizador(session);
        Map<String, Long> data = serviceReportes.contarEventosPorCategoria(usuario.getId());
        return ResponseEntity.ok(ApiResponse.ok("Reporte obtenido",
                Map.of("labels", data.keySet(), "data", data.values())));
    }

    private Usuario getOrganizador(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuario == null) throw new BusinessException("Debe iniciar sesión para acceder a los reportes");
        if (!usuario.getRol().getNombre().equalsIgnoreCase("organizador")) {
            throw new BusinessException("Solo los organizadores pueden acceder a los reportes");
        }
        return usuario;
    }
}