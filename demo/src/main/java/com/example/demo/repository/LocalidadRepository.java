package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Localidad;

@Repository
public interface LocalidadRepository extends JpaRepository<Localidad, Long>{
    List<Localidad> findByEventoId(Long eventoId);
    
    @Query("SELECT l FROM Localidad l WHERE l.evento.usuario.id = :organizadorId")
    List<Localidad> findByEventoUsuarioId(@Param("organizadorId") Long organizadorId);
    
    @Query("SELECT COUNT(l) FROM Localidad l WHERE l.evento.usuario.id = :organizadorId")
    long countByEventoUsuarioId(@Param("organizadorId") Long organizadorId);
}