package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.EventoDeseado;

@Repository
public interface EventoDeseadoRepository extends MongoRepository<EventoDeseado, String> {
    List<EventoDeseado> findByUsuarioId(String usuarioId);
    Page<EventoDeseado> findByUsuarioId(String usuarioId, Pageable pageable);
}
