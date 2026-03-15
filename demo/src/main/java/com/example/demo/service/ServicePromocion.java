package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Promocion;
import com.example.demo.repository.PromocionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicePromocion {

    private final PromocionRepository promocionRepository;

    public List<Promocion> obtenerPromociones() {
            return promocionRepository.findAll();
    }

    public void crearPromocion(Promocion nuevaPromocion) {
        promocionRepository.save(nuevaPromocion);
    }

    public Promocion obtenerPromocionPorId(Long id) {
        return promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("promocion no encontrada con id: " + id));
    }

    public void actualizarPromocion(Promocion promocionExistente) {
       promocionRepository.save(promocionExistente); 
    }

    public void eliminarPromocion(Long id) {
        promocionRepository.deleteById(id);
    }

    //numero de promociones por organizador
    public long contarPorOrganizador(Long organizadorId) {
        return promocionRepository.countByEventoUsuarioId(organizadorId);
    }

    //promociones por organizador logeado
    public List<Promocion> obtenerPorOrganizador(Long organizadorId) {
    return promocionRepository.findByEventoUsuarioId(organizadorId);
    }

    public List<Promocion> obtnerPromcionesPorEvento(Long eventoId) {
        return promocionRepository.findByEventoId(eventoId);
    }
}
