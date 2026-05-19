package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.model.Evento;

public interface EventoRepositoryCustom {
    Page<Evento> findAllWithReferences(Pageable pageable);

    Page<Evento> findByOrganizadorIdWithReferences(String organizadorId, Pageable pageable);

    Page<Evento> findByOrganizadorIdAndTituloContainingIgnoreCaseWithReferences(String organizadorId,
            String titulo, Pageable pageable);

    Page<Evento> findByTituloContainingIgnoreCaseWithReferences(String titulo, Pageable pageable);

    Page<Evento> findByCategoriaIdWithReferences(String categoriaId, Pageable pageable);

    Page<Evento> findByEstadoIdWithReferences(String estadoId, Pageable pageable);

    Evento findByIdWithReferences(String id);
}
