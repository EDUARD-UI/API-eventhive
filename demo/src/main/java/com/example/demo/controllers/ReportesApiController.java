package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.reportes.AsistentesDto;
import com.example.demo.dto.reportes.EventosCategoriaDto;
import com.example.demo.dto.reportes.OcupacionDto;
import com.example.demo.dto.reportes.UsuariosRolDto;
import com.example.demo.dto.reportes.ValoracionesDto;
import com.example.demo.dto.reportes.VentasEventoDto;
import com.example.demo.dto.reportes.VentasFechaDto;
import com.example.demo.service.ReportesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Slf4j
public class ReportesApiController {
    
    private final ReportesService reportesService;
    
    @GetMapping("/ventas")
    public ResponseEntity<List<VentasFechaDto>> obtenerVentas() {
        log.info("GET /api/reportes/ventas");
        List<VentasFechaDto> ventas = reportesService.obtenerVentasPorFecha();
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/ventas-evento")
    public ResponseEntity<List<VentasEventoDto>> obtenerVentasPorEvento() {
        log.info("GET /api/reportes/ventas-evento");
        List<VentasEventoDto> ventas = reportesService.obtenerVentasPorEvento();
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/usuarios-roles")
    public ResponseEntity<List<UsuariosRolDto>> obtenerUsuariosPorRol() {
        log.info("GET /api/reportes/usuarios-roles");
        List<UsuariosRolDto> usuarios = reportesService.obtenerUsuariosPorRol();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/eventos-categoria")
    public ResponseEntity<List<EventosCategoriaDto>> obtenerEventosPorCategoria() {
        log.info("GET /api/reportes/eventos-categoria");
        List<EventosCategoriaDto> eventos = reportesService.obtenerEventosPorCategoria();
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/ocupacion")
    public ResponseEntity<List<OcupacionDto>> obtenerOcupacionEventos() {
        log.info("GET /api/reportes/ocupacion");
        List<OcupacionDto> ocupacion = reportesService.obtenerOcupacionEventos();
        return ResponseEntity.ok(ocupacion);
    }
    
    @GetMapping("/valoraciones")
    public ResponseEntity<List<ValoracionesDto>> obtenerValoracionesEventos() {
        log.info("GET /api/reportes/valoraciones");
        List<ValoracionesDto> valoraciones = reportesService.obtenerValoracionesEventos();
        return ResponseEntity.ok(valoraciones);
    }


    @GetMapping("/organizador/{organizadorId}/ventas")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<List<VentasEventoDto>> obtenerVentasPorOrganizador(
            @PathVariable Long organizadorId) {
        log.info("GET /api/reportes/organizador/{}/ventas", organizadorId);
        List<VentasEventoDto> ventas = reportesService.obtenerVentasPorOrganizador(organizadorId);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/organizador/{organizadorId}/asistentes")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<List<AsistentesDto>> obtenerAsistentesPorOrganizador(
            @PathVariable Long organizadorId) {
        log.info("GET /api/reportes/organizador/{}/asistentes", organizadorId);
        List<AsistentesDto> asistentes = reportesService.obtenerAsistentesPorOrganizador(organizadorId);
        return ResponseEntity.ok(asistentes);
    }
}
