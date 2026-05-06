package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.reportes.CompradoresFrecuentesDto;
import com.example.demo.dto.reportes.EventosCategoriaDto;
import com.example.demo.dto.reportes.EventosEstadoDto;
import com.example.demo.dto.reportes.IngresosCategoriaDto;
import com.example.demo.dto.reportes.MetodoPagoDto;
import com.example.demo.dto.reportes.OcupacionDto;
import com.example.demo.dto.reportes.TicketPromedioDto;
import com.example.demo.dto.reportes.UsuariosRolDto;
import com.example.demo.dto.reportes.ValoracionIngresoDto;
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

    // Retorna clientes con más de una compra para analizar retención y fidelidad
    @GetMapping("/compradores-frecuentes")
    public ResponseEntity<List<CompradoresFrecuentesDto>> obtenerCompradoresFrecuentes() {
        log.info("GET /api/reportes/compradores-frecuentes");
        List<CompradoresFrecuentesDto> compradores = reportesService.obtenerCompradoresFrecuentes();
        return ResponseEntity.ok(compradores);
    }

    // Retorna el método de pago más usado agrupado, útil para decisiones financieras
    @GetMapping("/metodos-pago")
    public ResponseEntity<List<MetodoPagoDto>> obtenerMetodosPago() {
        log.info("GET /api/reportes/metodos-pago");
        List<MetodoPagoDto> metodos = reportesService.obtenerMetodosPago();
        return ResponseEntity.ok(metodos);
    }

    // Retorna la correlación entre promedio de calificación e ingresos por evento
    @GetMapping("/valoracion-vs-ingresos")
    public ResponseEntity<List<ValoracionIngresoDto>> obtenerValoracionVsIngresos() {
        log.info("GET /api/reportes/valoracion-vs-ingresos");
        List<ValoracionIngresoDto> data = reportesService.obtenerValoracionVsIngresos();
        return ResponseEntity.ok(data);
    }

    // Retorna eventos agrupados por estado (activo, cancelado, finalizado) para control operativo
    @GetMapping("/eventos-por-estado")
    public ResponseEntity<List<EventosEstadoDto>> obtenerEventosPorEstado() {
        log.info("GET /api/reportes/eventos-por-estado");
        List<EventosEstadoDto> estados = reportesService.obtenerEventosPorEstado();
        return ResponseEntity.ok(estados);
    }

    // Retorna el ticket promedio de compra por mes para detectar tendencias de gasto
    @GetMapping("/ticket-promedio")
    public ResponseEntity<List<TicketPromedioDto>> obtenerTicketPromedio() {
        log.info("GET /api/reportes/ticket-promedio");
        List<TicketPromedioDto> ticket = reportesService.obtenerTicketPromedio();
        return ResponseEntity.ok(ticket);
    }
}
