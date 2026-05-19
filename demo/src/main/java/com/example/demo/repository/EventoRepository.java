package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.NombreEventoDTO;
import com.example.demo.dto.reportes.EventosCategoriaDto;
import com.example.demo.model.Evento;

@Repository
public interface EventoRepository extends MongoRepository<Evento, String>, EventoRepositoryCustom {

    List<Evento> findTop3ByOrderByFechaAsc();

    List<Evento> findByCategoriaId(String categoriaId);

    List<Evento> findByTituloContainingIgnoreCase(String titulo);

    List<Evento> findByOrganizadorId(String organizadorId);

    long countByOrganizadorId(String organizadorId);

    long countByCategoriaId(String id);

    long countByEstadoId(String id);

    @Query("{ 'organizador._id': ?0 }")
    List<NombreEventoDTO> findNombresByOrganizadorId(String organizadorId);

    @Override
    Page<Evento> findAll(Pageable pageable);

    Page<Evento> findByCategoriaId(String categoriaId, Pageable pageable);

    Page<Evento> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    Page<Evento> findByOrganizadorId(String organizadorId, Pageable pageable);

    Page<Evento> findByEstadoId(String estadoId, Pageable pageable);

    Page<Evento> findByOrganizadorIdAndTituloContainingIgnoreCase(String organizadorId, String titulo, Pageable pageable);

    @Aggregation(pipeline = {
        "{ $addFields: { categoriaObjectId: { $toObjectId: '$categoria._id' } } }",
        "{ $lookup: { from: 'categorias', localField: 'categoriaObjectId', foreignField: '_id', as: 'categoriaData' } }",
        "{ $unwind: '$categoriaData' }",
        "{ $group: { _id: '$categoriaData.nombre', total: { $sum: 1 } } }",
        "{ $sort: { total: -1 } }"
    })
    List<EventosCategoriaDto> obtenerEventosPorCategoria();
}
