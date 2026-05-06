package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.reportes.ValoracionIngresoDto;
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
@Aggregation(pipeline = {
        "{ $match: { calificacion: { $exists: true } } }",
        "{ $addFields: { eventoOid: { $ifNull: [ '$evento._id', null ] } } }",
        "{ $lookup: { from: 'eventos', localField: 'eventoOid', foreignField: '_id', as: 'eventoData' } }",
        "{ $unwind: '$eventoData' }",
        "{ $group: { _id: '$eventoData._id', evento: { $first: '$eventoData.titulo' }, promedio: { $avg: '$calificacion' }, totalReviews: { $sum: 1 } } }",
        "{ $project: { _id: 0, evento: 1, promedio: { $round: [ '$promedio', 2 ] }, totalReviews: 1 } }",
        "{ $sort: { promedio: -1 } }"
    })
    List<ValoracionesDto> obtenerValoracionesEventos();

    @Aggregation(pipeline = {
        "{ $match: { calificacion: { $exists: true } } }",
        "{ $addFields: { eventoOid: { $ifNull: [ '$evento._id', null ] } } }",
        "{ $lookup: { from: 'eventos', localField: 'eventoOid', foreignField: '_id', as: 'eventoData' } }",
        "{ $unwind: '$eventoData' }",
        "{ $addFields: { eventoIdStr: { $toString: '$eventoData._id' } } }",
        "{ $lookup: { from: 'compras', localField: 'eventoIdStr', foreignField: 'items.eventoId', as: 'comprasData' } }",
        "{ $group: { _id: '$eventoData._id', evento: { $first: '$eventoData.titulo' }, promedioCalificacion: { $avg: '$calificacion' }, totalReviews: { $sum: 1 }, ingresos: { $sum: { $sum: '$comprasData.total' } } } }",
        "{ $project: { _id: 0, evento: 1, promedioCalificacion: { $round: [ '$promedioCalificacion', 2 ] }, totalReviews: 1, ingresos: 1 } }",
        "{ $sort: { promedioCalificacion: -1 } }"
    })
    List<ValoracionIngresoDto> obtenerValoracionVsIngresos();
}
