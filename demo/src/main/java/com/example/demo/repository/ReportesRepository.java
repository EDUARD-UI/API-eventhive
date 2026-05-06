package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.reportes.OcupacionDto;
import com.example.demo.dto.reportes.VentasEventoDto;
import com.example.demo.dto.reportes.VentasFechaDto;
import com.example.demo.model.Compra;

@Repository
public interface ReportesRepository extends MongoRepository<Compra, String> {

    @Aggregation(pipeline = {
        "{ $match: { fechaCompra: { $exists: true }, items: { $exists: true, $ne: [] } } }",
        "{ $group: { _id: { $dateToString: { format: '%Y-%m-%d', date: '$fechaCompra' } }, "
        + "totalVentas: { $sum: 1 }, ingresos: { $sum: '$total' } } }",
        "{ $project: { _id: 0, fecha: '$_id', totalVentas: 1, ingresos: 1 } }",
        "{ $sort: { fecha: -1 } }"
    })
    List<VentasFechaDto> obtenerVentasPorFecha();

    @Aggregation(pipeline = {
        "{ $match: { items: { $exists: true, $ne: [] } } }",
        "{ $unwind: '$items' }",
        "{ $lookup: { from: 'eventos', localField: 'items.eventoId', foreignField: '_id', as: 'evento' } }",
        "{ $unwind: { path: '$evento', preserveNullAndEmptyArrays: false } }",
        "{ $group: { _id: '$evento.titulo', ventas: { $sum: '$items.cantidad' }, "
        + "ingresos: { $sum: { $multiply: [ '$items.cantidad', '$items.precioUnitario' ] } } } }",
        "{ $project: { _id: 0, evento: '$_id', ventas: 1, ingresos: 1 } }",
        "{ $sort: { ingresos: -1 } }"
    })
    List<VentasEventoDto> obtenerVentasPorEvento();

    @Aggregation(pipeline = {
        "{ $match: { items: { $exists: true, $ne: [] } } }",
        "{ $unwind: '$items' }",
        "{ $lookup: { from: 'eventos', localField: 'items.eventoId', foreignField: '_id', as: 'evento' } }",
        "{ $unwind: { path: '$evento', preserveNullAndEmptyArrays: false } }",
        "{ $unwind: { path: '$evento.localidades', preserveNullAndEmptyArrays: true } }",
        "{ $match: { $expr: { $eq: [ '$items.localidadId', '$evento.localidades.id' ] } } }",
        "{ $group: { _id: '$evento._id', evento: { $first: '$evento.titulo' }, "
        + "capacidad: { $first: '$evento.localidades.capacidad' }, vendidos: { $sum: '$items.cantidad' } } }",
        "{ $project: { _id: 0, evento: 1, capacidad: 1, vendidos: 1, "
        + "ocupacion: { $round: [ { $multiply: [ { $divide: [ '$vendidos', '$capacidad' ] }, 100 ] }, 2 ] } } }",
        "{ $sort: { ocupacion: -1 } }"
    })
    List<OcupacionDto> obtenerOcupacionEventos();
}
