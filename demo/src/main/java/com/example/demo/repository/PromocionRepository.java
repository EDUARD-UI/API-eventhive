package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Promocion;

@Repository
public interface PromocionRepository extends MongoRepository<Promocion, String> {

    // Buscar promociones por ID de evento (un evento puede tener UNA promoción)
    @Query("{ 'eventos._id': ?0 }")
    List<Promocion> findByEventosId(String eventoId);
    
    // Buscar primera promoción por ID de evento
    @Query("{ 'eventos._id': ?0 }")
    Promocion findFirstByEventosId(String eventoId);

    // Buscar promociones por lista de IDs de eventos
    @Query("{ 'eventos._id': { $in: ?0 } }")
    Page<Promocion> findByEventosIdIn(List<String> eventoIds, Pageable pageable);

    // Buscar promociones por organizador
    @Query("{ 'eventos.organizador._id': ?0 }")
    List<Promocion> findByEventosOrganizadorId(String organizadorId);

    @Query("{ 'eventos.organizador._id': ?0 }")
    Page<Promocion> findByEventosOrganizadorId(String organizadorId, Pageable pageable);
}