package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.reportes.EventosCategoriaDto;
import com.example.demo.dto.reportes.IngresosCategoriaDto;
import com.example.demo.dto.reportes.OcupacionDto;
import com.example.demo.dto.reportes.TicketPromedioDto;
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

    // Retorna eventos con ocupación crítica (< 30%) para alertas de riesgo
    @GetMapping("/eventos-riesgo")
    public ResponseEntity<List<OcupacionDto>> obtenerEventosEnRiesgo() {
        log.info("GET /api/reportes/eventos-riesgo");
        List<OcupacionDto> riesgo = reportesService.obtenerEventosEnRiesgo();
        return ResponseEntity.ok(riesgo);
    }

    // Retorna ingresos agrupados por categoria para medir rendimiento por tipo de evento
    @GetMapping("/ingresos-categoria")
    public ResponseEntity<List<IngresosCategoriaDto>> obtenerIngresosPorCategoria() {
        log.info("GET /api/reportes/ingresos-categoria");
        List<IngresosCategoriaDto> ingresos = reportesService.obtenerIngresosPorCategoria();
        return ResponseEntity.ok(ingresos);
    }

    // Retorna el ticket promedio de compra por mes para detectar tendencias de gasto
    @GetMapping("/ticket-promedio")
    public ResponseEntity<List<TicketPromedioDto>> obtenerTicketPromedio() {
        log.info("GET /api/reportes/ticket-promedio");
        List<TicketPromedioDto> ticket = reportesService.obtenerTicketPromedio();
        return ResponseEntity.ok(ticket);
    }
}
