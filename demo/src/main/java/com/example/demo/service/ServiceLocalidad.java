package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Localidad;
import com.example.demo.repository.LocalidadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceLocalidad {

    private final LocalidadRepository localidadRepository;

    public List<Localidad> obtenerLocalidadesPorEvento(Long eventoId) {
        return localidadRepository.findByEventoId(eventoId);
    }

    public List<Localidad> obtenerTodasLasLocalidades() {
        return localidadRepository.findAll();
    }

    public void crearLocalidad(Localidad nuevaLocalidad) {
        localidadRepository.save(nuevaLocalidad);
    }

    public Localidad obtenerLocalidadPorId(Long id) {
        return localidadRepository.findById(id).orElse(null);
    }

    public void actualizarLocalidad(Localidad localidadExistente) {
        localidadRepository.save(localidadExistente);
    }

    public void eliminarLocalidad(Long id) {
        localidadRepository.deleteById(id);
    }

    public List<Localidad> obtenerPorOrganizador(Long organizadorId) {
        return localidadRepository.findByEventoUsuarioId(organizadorId);
    }

    public long contarPorOrganizador(Long organizadorId) {
        return localidadRepository.countByEventoUsuarioId(organizadorId);
    }
}