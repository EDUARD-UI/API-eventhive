package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.reportes.EventosCategoriaDto;
import com.example.demo.dto.reportes.IngresosCategoriaDto;
import com.example.demo.dto.reportes.MetodoPagoDto;
import com.example.demo.dto.reportes.OcupacionDto;
import com.example.demo.dto.reportes.TicketPromedioDto;
import com.example.demo.dto.reportes.UsuariosRolDto;
import com.example.demo.dto.reportes.VentasEventoDto;
import com.example.demo.dto.reportes.VentasFechaDto;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.ReportesRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ReportesService {

    private final ReportesRepository reportesRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;

    public List<VentasFechaDto> obtenerVentasPorFecha() {
        return reportesRepository.obtenerVentasPorFecha();
    }

    public List<VentasEventoDto> obtenerVentasPorEvento() {
        return reportesRepository.obtenerVentasPorEvento();
    }

    public List<UsuariosRolDto> obtenerUsuariosPorRol() {
        return usuarioRepository.obtenerUsuariosPorRol();
    }

    public List<EventosCategoriaDto> obtenerEventosPorCategoria() {
        return eventoRepository.obtenerEventosPorCategoria();
    }

    public List<OcupacionDto> obtenerOcupacionEventos() {
        return reportesRepository.obtenerOcupacionEventos();
    }

    // Eventos con ocupacion critica — para alertar al admin sobre posibles fracasos
    public List<OcupacionDto> obtenerEventosEnRiesgo() {
        return reportesRepository.obtenerEventosEnRiesgo();
    }

    // Ingresos por categoria — para saber que tipo de eventos son mas rentables
    public List<IngresosCategoriaDto> obtenerIngresosPorCategoria() {
        return reportesRepository.obtenerIngresosPorCategoria();
    }

    // Metodos de pago usados — para analisis financiero y fricciones en checkout
    public List<MetodoPagoDto> obtenerMetodosPago() {
        return reportesRepository.obtenerMetodosPago();
    }

    // Ticket promedio mensual — para detectar tendencias de gasto del usuario
    public List<TicketPromedioDto> obtenerTicketPromedio() {
        return reportesRepository.obtenerTicketPromedio();
    }

}
