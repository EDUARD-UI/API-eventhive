package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Evento;
import com.example.demo.model.Localidad;
import com.example.demo.repository.LocalidadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceLocalidad {

    private final LocalidadRepository localidadRepository;
    private final ServiceEvento serviceEvento;

    public List<Localidad> obtenerTodasLasLocalidades()            { return localidadRepository.findAll(); }
    public List<Localidad> obtenerLocalidadesPorEvento(Long evId)  { return localidadRepository.findByEventoId(evId); }
    public List<Localidad> obtenerPorOrganizador(Long orgId)       { return localidadRepository.findByEventoUsuarioId(orgId); }
    public long contarPorOrganizador(Long orgId)                   { return localidadRepository.countByEventoUsuarioId(orgId); }

    public Localidad obtenerLocalidadPorId(Long id) {
        return localidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La localidad no existe"));
    }

    // valida disponibles <= capacidad, construye y guarda
    public void crearLocalidad(String nombre, BigDecimal precio,
                                Integer capacidad, Integer disponibles, Long eventoId) {
        if (disponibles > capacidad)
            throw new BusinessException("Los asientos disponibles no pueden ser mayores que la capacidad");

        Evento evento = serviceEvento.obtenerEventoPorId(eventoId);

        Localidad loc = new Localidad();
        loc.setNombre(nombre);
        loc.setPrecio(precio);
        loc.setCapacidad(capacidad);
        loc.setDisponibles(disponibles);
        loc.setEvento(evento);
        localidadRepository.save(loc);
    }

    // valida existencia, disponibles <= capacidad, actualiza y guarda
    public void actualizarLocalidad(Long id, String nombre, BigDecimal precio,
                                     Integer capacidad, Integer disponibles, Long eventoId) {
        Localidad existente = obtenerLocalidadPorId(id);

        if (disponibles > capacidad)
            throw new BusinessException("Los asientos disponibles no pueden ser mayores que la capacidad");

        Evento evento = serviceEvento.obtenerEventoPorId(eventoId);
        existente.setNombre(nombre);
        existente.setPrecio(precio);
        existente.setCapacidad(capacidad);
        existente.setDisponibles(disponibles);
        existente.setEvento(evento);
        localidadRepository.save(existente);
    }

    public void eliminarLocalidad(Long id) {
        obtenerLocalidadPorId(id); // lanza si no existe
        localidadRepository.deleteById(id);
    }
}