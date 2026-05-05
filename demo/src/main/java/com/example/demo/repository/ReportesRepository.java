package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.reportes.AsistentesDto;
import com.example.demo.dto.reportes.VentasEventoDto;
import com.example.demo.dto.reportes.VentasFechaDto;
import com.example.demo.model.Compra;

@Repository
public interface ReportesRepository extends MongoRepository<Compra, String> {

    // Ventas agrupadas por fecha (parte de compras - ESTÁ BIEN)
    @Aggregation(pipeline = {
        "{ $group: { _id: { $dateToString: { format: '%Y-%m-%d', date: '$fechaCompra' } }, "
        + "totalVentas: { $sum: 1 }, ingresos: { $sum: '$monto' } } }",
        "{ $project: { _id: 0, fecha: '$_id', totalVentas: 1, ingresos: 1 } }",
        "{ $sort: { fecha: -1 } }"
    })
    List<VentasFechaDto> obtenerVentasPorFecha();

    // Ventas por organizador (parte de compras - ESTÁ BIEN)
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'items_compra', localField: '_id', foreignField: 'compraId', as: 'items' } }",
        "{ $unwind: '$items' }",
        "{ $lookup: { from: 'eventos', localField: 'items.eventoId', foreignField: '_id', as: 'evento' } }",
        "{ $unwind: '$evento' }",
        "{ $match: { 'evento.organizador.$id': { $oid: ?0 } } }",
        "{ $group: { _id: '$evento._id', evento: { $first: '$evento.nombre' }, ventas: { $sum: '$items.cantidad' }, ingresos: { $sum: { $multiply: [ '$items.cantidad', '$items.precioUnitario' ] } } } }",
        "{ $project: { _id: 0, evento: 1, ventas: 1, ingresos: 1 } }",
        "{ $sort: { ingresos: -1 } }"
    })
    List<VentasEventoDto> obtenerVentasPorOrganizador(String organizadorId);

    // Asistentes por organizador (parte de compras - ESTÁ BIEN)
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'items_compra', localField: '_id', foreignField: 'compraId', as: 'items' } }",
        "{ $unwind: '$items' }",
        "{ $lookup: { from: 'eventos', localField: 'items.eventoId', foreignField: '_id', as: 'evento' } }",
        "{ $unwind: '$evento' }",
        "{ $match: { 'evento.organizador.$id': { $oid: ?0 } } }",
        "{ $group: { _id: '$evento._id', evento: { $first: '$evento.nombre' }, asistentes: { $sum: '$items.cantidad' } } }",
        "{ $project: { _id: 0, evento: 1, asistentes: 1 } }",
        "{ $sort: { asistentes: -1 } }"
    })
    List<AsistentesDto> obtenerAsistentesPorOrganizador(String organizadorId);
}