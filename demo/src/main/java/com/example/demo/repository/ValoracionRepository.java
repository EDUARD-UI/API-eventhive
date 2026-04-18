package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Valoracion;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long>{
    Long countByEventoId(Long id);
    List<Valoracion> findTop3ByOrderById();
    List<Valoracion> findByClienteIdOrderByIdDesc(Long clienteId);
    Long countByClienteId(Long clienteId);
    List<Valoracion> findByEventoIdOrderByIdDesc(Long eventoId);
    Page<Valoracion> findByClienteIdOrderByIdDesc(Long clienteId, Pageable pageable);
    Page<Valoracion> findByEventoIdOrderByIdDesc(Long eventoId, Pageable pageable);
    
    @Query("SELECT COUNT(v) FROM Valoracion v WHERE v.evento.usuario.id = :organizadorId")
    Long countByEventoUsuarioId(@Param("organizadorId") Long organizadorId);
}