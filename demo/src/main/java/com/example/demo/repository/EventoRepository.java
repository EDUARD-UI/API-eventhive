package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.NombreEventoDTO;
import com.example.demo.model.Evento;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    
    // Métodos sin paginación
    List<Evento> findTop3ByOrderByFechaAsc();
    List<Evento> findByCategoriaId(Long categoriaId);
    List<Evento> findByTituloContainingIgnoreCase(String titulo);
    List<Evento> findByUsuarioId(Long organizadorId);
    long countByUsuarioId(Long organizadorId);
    Long countByCategoriaId(Long id);
    Long countByEstadoId(Long id);

    @Query("SELECT new com.example.demo.dto.NombreEventoDTO(e.id, e.titulo) FROM Evento e WHERE e.usuario.id = :organizadorId")
    List<NombreEventoDTO> findNombresByOrganizadorId(@Param("organizadorId") Long organizadorId);
    
    // Búsqueda por título limitada a 5 registros
    @Query(value = "SELECT e FROM Evento e WHERE LOWER(e.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')) " +
                   "ORDER BY e.titulo ASC",
           nativeQuery = false)
    List<Evento> findByTituloLimitado(@Param("titulo") String titulo, Pageable pageable);
    
    // Métodos paginados
    @Override
    Page<Evento> findAll(Pageable pageable);
    Page<Evento> findByCategoriaId(Long categoriaId, Pageable pageable);
    Page<Evento> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
    Page<Evento> findByUsuarioId(Long organizadorId, Pageable pageable);
}
