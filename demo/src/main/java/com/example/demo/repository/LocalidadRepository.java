package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Localidad;

@Repository
public interface LocalidadRepository extends JpaRepository<Localidad, Long> {
    
    // Métodos sin paginación
    List<Localidad> findByEventoId(Long eventoId);
    
    // Métodos paginados
    @Override
    Page<Localidad> findAll(Pageable pageable);
    Page<Localidad> findByEventoId(Long eventoId, Pageable pageable);
    
    @Query("SELECT l FROM Localidad l WHERE l.evento.usuario.id = :organizadorId")
    Page<Localidad> findByEventoUsuarioId(@Param("organizadorId") Long organizadorId, Pageable pageable);
    
    @Query("SELECT COUNT(l) FROM Localidad l WHERE l.evento.usuario.id = :organizadorId")
    long countByEventoUsuarioId(@Param("organizadorId") Long organizadorId);
    
    @Query("SELECT COUNT(l) FROM Localidad l WHERE l.evento.id = :eventoId")
    long countByEventoId(@Param("eventoId") Long eventoId);
}
