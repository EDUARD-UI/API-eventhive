package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Localidad;

@Repository
public interface LocalidadRepository extends MongoRepository<Localidad, String> {
    
    // Métodos sin paginación
    List<Localidad> findByEventoId(String eventoId);
    
    // Métodos paginados
    @Override
    Page<Localidad> findAll(Pageable pageable);
    Page<Localidad> findByEventoId(String eventoId, Pageable pageable);
    
    @Query("{ 'evento.usuario._id': ?0 }")
    Page<Localidad> findByEventoUsuarioId(String organizadorId, Pageable pageable);
    
    @Query("{ 'evento.usuario._id': ?0 }")
    long countByEventoUsuarioId(String organizadorId);
    
    long countByEventoId(String eventoId);
}
