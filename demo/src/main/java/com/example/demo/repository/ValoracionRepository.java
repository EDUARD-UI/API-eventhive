package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.reportes.ValoracionesDto;
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

    // Valoraciones de eventos (parte de valoraciones)
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'eventos', localField: 'evento._id', foreignField: '_id', as: 'evento' } }",
        "{ $unwind: '$evento' }",
        "{ $group: { _id: '$evento._id', evento: { $first: '$evento.nombre' }, "
        + "promedio: { $avg: '$puntuacion' }, totalReviews: { $sum: 1 } } }",
        "{ $project: { _id: 0, evento: 1, promedio: 1, totalReviews: 1 } }",
        "{ $sort: { promedio: -1 } }"
    })
    List<ValoracionesDto> obtenerValoracionesEventos();
}
