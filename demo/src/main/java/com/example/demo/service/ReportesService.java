package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.reportes.AsistentesDto;
import com.example.demo.dto.reportes.EventosCategoriaDto;
import com.example.demo.dto.reportes.OcupacionDto;
import com.example.demo.dto.reportes.UsuariosRolDto;
import com.example.demo.dto.reportes.ValoracionesDto;
import com.example.demo.dto.reportes.VentasEventoDto;
import com.example.demo.dto.reportes.VentasFechaDto;
import com.example.demo.repository.ReportesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ReportesService {
    
    private final ReportesRepository reportesRepository;
    
    public List<VentasFechaDto> obtenerVentasPorFecha() {
        return reportesRepository.obtenerVentasPorFecha();
    }
    
    public List<VentasEventoDto> obtenerVentasPorEvento() {
        return reportesRepository.obtenerVentasPorEvento();
    }
    
    public List<UsuariosRolDto> obtenerUsuariosPorRol() {
        return reportesRepository.obtenerUsuariosPorRol();
    }
    
    public List<EventosCategoriaDto> obtenerEventosPorCategoria() {
        return reportesRepository.obtenerEventosPorCategoria();
    }
    
    public List<OcupacionDto> obtenerOcupacionEventos() {
        return reportesRepository.obtenerOcupacionEventos();
    }
    
    public List<VentasEventoDto> obtenerVentasPorOrganizador(Long organizadorId) {
        return reportesRepository.obtenerVentasPorOrganizador(organizadorId);
    }
    
    public List<AsistentesDto> obtenerAsistentesPorOrganizador(Long organizadorId) {
        return reportesRepository.obtenerAsistentesPorOrganizador(organizadorId);
    }
    
    public List<ValoracionesDto> obtenerValoracionesEventos() {
        return reportesRepository.obtenerValoracionesEventos();
    }
}
