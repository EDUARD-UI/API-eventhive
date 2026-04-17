package com.example.demo.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.PagedResponse;
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

    public long contarPorOrganizador(Long orgId) {
        return localidadRepository.countByEventoUsuarioId(orgId);
    }

    public Localidad obtenerLocalidadPorId(Long id) {
        return localidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La localidad no existe"));
    }

    public void crearLocalidad(String nombre, BigDecimal precio,
            Integer capacidad, Integer disponibles, Long eventoId) {
        if (disponibles > capacidad) {
            throw new BusinessException("Los asientos disponibles no pueden ser mayores que la capacidad");
        }

        Evento evento = serviceEvento.obtenerEventoPorId(eventoId);

        Localidad loc = new Localidad();
        loc.setNombre(nombre);
        loc.setPrecio(precio);
        loc.setCapacidad(capacidad);
        loc.setDisponibles(disponibles);
        loc.setEvento(evento);
        localidadRepository.save(loc);
    }

    public void actualizarLocalidad(Long id, String nombre, BigDecimal precio,
            Integer capacidad, Integer disponibles, Long eventoId) {
        Localidad existente = obtenerLocalidadPorId(id);

        if (disponibles > capacidad) {
            throw new BusinessException("Los asientos disponibles no pueden ser mayores que la capacidad");
        }

        Evento evento = serviceEvento.obtenerEventoPorId(eventoId);
        existente.setNombre(nombre);
        existente.setPrecio(precio);
        existente.setCapacidad(capacidad);
        existente.setDisponibles(disponibles);
        existente.setEvento(evento);
        localidadRepository.save(existente);
    }

    public void eliminarLocalidad(Long id) {
        obtenerLocalidadPorId(id);
        localidadRepository.deleteById(id);
    }

    public PagedResponse<Localidad> obtenerLocalidadesPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Localidad> pageResult = localidadRepository.findAll(pageable);

        return new PagedResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    public PagedResponse<Localidad> obtenerPorOrganizadorPaginado(Long organizadorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Localidad> pageResult = localidadRepository.findByEventoUsuarioId(organizadorId, pageable);

        return new PagedResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    public PagedResponse<Localidad> obtenerPorEventoPaginado(Long eventoId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Localidad> pageResult = localidadRepository.findByEventoId(eventoId, pageable);

        return new PagedResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    public boolean tieneLocalidades(Long eventoId) {
        return localidadRepository.countByEventoId(eventoId) > 0;
    }
}
