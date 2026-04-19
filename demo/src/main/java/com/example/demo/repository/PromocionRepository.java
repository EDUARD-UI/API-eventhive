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

    List<Promocion> findByEventoId(String eventoId);

    @Query("{ 'evento.usuario._id': ?0 }")
    List<Promocion> findByEventoUsuarioId(String organizadorId);

    @Query("{ 'evento.usuario._id': ?0 }")
    long countByEventoUsuarioId(String organizadorId);

    @Query("{ 'evento.usuario._id': ?0 }")
    Page<Promocion> findByEventoUsuarioIdPageable(String organizadorId, Pageable pageable);
}
