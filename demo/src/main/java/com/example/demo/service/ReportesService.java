package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.reportes.EventosCategoriaDto;
import com.example.demo.dto.reportes.OcupacionDto;
import com.example.demo.dto.reportes.UsuariosRolDto;
import com.example.demo.dto.reportes.ValoracionesDto;
import com.example.demo.dto.reportes.VentasEventoDto;
import com.example.demo.dto.reportes.VentasFechaDto;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.ReportesRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.ValoracionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ReportesService {

    private final ReportesRepository reportesRepository;
    private final UsuarioRepository usuarioRepository;
    private final ValoracionRepository valoracionRepository;
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

    public List<ValoracionesDto> obtenerValoracionesEventos() {
        return valoracionRepository.obtenerValoracionesEventos();
    }
}
