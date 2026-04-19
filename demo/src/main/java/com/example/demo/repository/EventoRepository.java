package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.NombreEventoDTO;
import com.example.demo.model.Evento;

@Repository
public interface EventoRepository extends MongoRepository<Evento, String> {
    
    // Métodos sin paginación
    List<Evento> findTop3ByOrderByFechaAsc();
    List<Evento> findByCategoriaId(String categoriaId);
    List<Evento> findByTituloContainingIgnoreCase(String titulo);
    List<Evento> findByUsuarioId(String organizadorId);
    long countByUsuarioId(String organizadorId);
    long countByCategoriaId(String id);
    long countByEstadoId(String id);

    @Query("{ 'usuario._id': ?0 }")
    List<NombreEventoDTO> findNombresByOrganizadorId(String organizadorId);
    
    // Búsqueda por título
    @Query("{ 'titulo': { \\$regex: ?0, \\$options: 'i' } }")
    List<Evento> findByTituloLimitado(String titulo, Pageable pageable);
    
    // Métodos paginados
    @Override
    Page<Evento> findAll(Pageable pageable);
    Page<Evento> findByCategoriaId(String categoriaId, Pageable pageable);
    Page<Evento> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
    Page<Evento> findByUsuarioId(String organizadorId, Pageable pageable);
}
