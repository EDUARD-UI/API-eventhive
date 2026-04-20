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

    Page<Promocion> findByEventoIdIn(List<String> eventoIds, Pageable pageable);

    @Query("{ 'evento.organizador._id': ?0 }")
    List<Promocion> findByEventoOrganizadorId(String organizadorId);

    @Query("{ 'evento.organizador._id': ?0 }")
    long countByEventoOrganizadorId(String organizadorId);

    @Query("{ 'evento.organizador._id': ?0 }")
    Page<Promocion> findByEventoOrganizadorId(String organizadorId, Pageable pageable);
}
