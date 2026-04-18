package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Estado;
import com.example.demo.repository.EstadoRepository;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceEstado {

    private final EstadoRepository estadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;

    public Estado findByNombre(String nombre) {
        return estadoRepository.findByNombre(nombre);
    }

    public List<Estado> obtenerEstados() {
        return estadoRepository.findAll();
    }

    public Page<Estado> obtenerEstados(Pageable pageable) {
        return estadoRepository.findAll(pageable);
    }

    public Estado obtenerEstadoPorId(Long id) {
        return estadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado"));
    }

    public boolean tieneEntidadesAsociadas(Long estadoId) {
        return usuarioRepository.countByEstadoId(estadoId) > 0
                || eventoRepository.countByEstadoId(estadoId) > 0;
    }

    public void crearEstado(String nombre, String descripcion) {
        if (findByNombre(nombre) != null) {
            throw new BusinessException("Ya existe un estado con ese nombre");
        }

        Estado nuevo = new Estado();
        nuevo.setNombre(nombre);
        nuevo.setDescripcion(descripcion);
        estadoRepository.save(nuevo);
    }

    public void actualizarEstado(Long id, String nombre, String descripcion) {
    Estado existente = obtenerEstadoPorId(id);
    
    Estado conNombre = findByNombre(nombre);
    if (conNombre != null && !conNombre.getId().equals(id)) {
        throw new BusinessException("Ya existe otro estado con ese nombre");
    }

    existente.setNombre(nombre);
    existente.setDescripcion(descripcion);
    estadoRepository.save(existente);
}

    public void eliminarEstado(Long id) {
        obtenerEstadoPorId(id); // Valida que exista
        
        if (tieneEntidadesAsociadas(id)) {
            throw new BusinessException("No se puede eliminar el estado porque está siendo utilizado");
        }

        estadoRepository.deleteById(id);
    }
}
