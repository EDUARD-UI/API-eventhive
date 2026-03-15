package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Promocion;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long>{

    List<Promocion> findByEventoId(Long eventoId);
    
    @Query("SELECT p FROM Promocion p WHERE p.evento.usuario.id = :organizadorId")
    List<Promocion> findByEventoUsuarioId(@Param("organizadorId") Long organizadorId);
    
    @Query("SELECT COUNT(p) FROM Promocion p WHERE p.evento.usuario.id = :organizadorId")
    long countByEventoUsuarioId(@Param("organizadorId") Long organizadorId);
}