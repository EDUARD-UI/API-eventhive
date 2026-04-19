package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Valoracion;

@Repository
public interface ValoracionRepository extends MongoRepository<Valoracion, String> {
    long countByEventoId(String id);
    List<Valoracion> findTop3ByOrderById();
    List<Valoracion> findByClienteIdOrderByIdDesc(String clienteId);
    long countByClienteId(String clienteId);
    List<Valoracion> findByEventoIdOrderByIdDesc(String eventoId);
    Page<Valoracion> findByClienteIdOrderByIdDesc(String clienteId, Pageable pageable);
    Page<Valoracion> findByEventoIdOrderByIdDesc(String eventoId, Pageable pageable);
    
    @Query("{ 'evento.usuario._id': ?0 }")
    long countByEventoUsuarioId(String organizadorId);
}