package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.EventoDeseado;

@Repository
public interface EventoDeseadoRepository extends JpaRepository<EventoDeseado, Long> {
    List<EventoDeseado> findByUsuarioId(Long usuarioId);
    Page<EventoDeseado> findByUsuarioId(Long usuarioId, Pageable pageable);

}
