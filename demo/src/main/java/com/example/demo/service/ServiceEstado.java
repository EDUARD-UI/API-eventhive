package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Estado;
import com.example.demo.repository.EstadoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceEstado {

    private final EstadoRepository estadoRepository;

    public Estado findByNombre(String nombre) {
        return estadoRepository.findByNombre(nombre);
    }

    public List<Estado> obtenerEstados() {
        return estadoRepository.findAll();
    }

    public Page<Estado> obtenerEstados(Pageable pageable) {
        return estadoRepository.findAll(pageable);
    }

    public Estado obtenerEstadoPorId(String id) {
        return estadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado"));
    }

    public void crearEstado(String nombre, String descripcion) {
        if (estadoRepository.existsByNombre(nombre)) {
            throw new BusinessException("Ya existe un estado con ese nombre");
        }

        Estado nuevo = new Estado();
        nuevo.setNombre(nombre);
        nuevo.setDescripcion(descripcion);
        estadoRepository.save(nuevo);
    }

    public void actualizarEstado(String id, String nombre, String descripcion) {
        Estado existente = obtenerEstadoPorId(id);

        if (estadoRepository.existsByNombre(nombre) && 
            !nombre.equals(existente.getNombre())) {
            throw new BusinessException("Ya existe otro estado con ese nombre");
        }

        existente.setNombre(nombre);
        existente.setDescripcion(descripcion);
        estadoRepository.save(existente);
    }

    public void eliminarEstado(String id) {
        obtenerEstadoPorId(id);
        estadoRepository.deleteById(id);
    }
}
