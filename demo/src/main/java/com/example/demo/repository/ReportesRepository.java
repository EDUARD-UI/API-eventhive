package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.reportes.*;
import com.example.demo.model.Compra;

@Repository
public interface ReportesRepository extends MongoRepository<Compra, String> {

    // Ventas agrupadas por fecha
    @Aggregation(pipeline = {
        "{ $group: { _id: { $dateToString: { format: '%Y-%m-%d', date: '$fechaCompra' } }, " +
        "totalVentas: { $sum: 1 }, ingresos: { $sum: '$monto' } } }",
        "{ $project: { _id: 0, fecha: '$_id', totalVentas: 1, ingresos: 1 } }",
        "{ $sort: { fecha: -1 } }"
    })
    List<VentasFechaDto> obtenerVentasPorFecha();

    // Ventas agrupadas por evento
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'boletos', localField: '_id', foreignField: 'compraId', as: 'boletos' } }",
        "{ $unwind: '$boletos' }",
        "{ $lookup: { from: 'eventos', localField: 'boletos.eventoId', foreignField: '_id', as: 'evento' } }",
        "{ $unwind: '$evento' }",
        "{ $group: { _id: '$evento.nombre', ventas: { $sum: 1 }, ingresos: { $sum: '$monto' } } }",
        "{ $project: { _id: 0, evento: '$_id', ventas: 1, ingresos: 1 } }",
        "{ $sort: { ingresos: -1 } }"
    })
    List<VentasEventoDto> obtenerVentasPorEvento();

    // Usuarios agrupados por rol
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'usuarios', localField: 'usuarioId', foreignField: '_id', as: 'usuario' } }",
        "{ $unwind: '$usuario' }",
        "{ $group: { _id: '$usuario.rol', cantidad: { $sum: 1 } } }",
        "{ $project: { _id: 0, rol: '$_id', cantidad: 1 } }",
        "{ $sort: { rol: 1 } }"
    })
    List<UsuariosRolDto> obtenerUsuariosPorRol();

    // Eventos agrupados por categoría
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'boletos', localField: '_id', foreignField: 'compraId', as: 'boletos' } }",
        "{ $unwind: '$boletos' }",
        "{ $lookup: { from: 'eventos', localField: 'boletos.eventoId', foreignField: '_id', as: 'evento' } }",
        "{ $unwind: '$evento' }",
        "{ $group: { _id: '$evento.categoria', total: { $sum: 1 } } }",
        "{ $project: { _id: 0, categoria: '$_id', total: 1 } }",
        "{ $sort: { total: -1 } }"
    })
    List<EventosCategoriaDto> obtenerEventosPorCategoria();

    // Ocupación de eventos
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'boletos', localField: '_id', foreignField: 'compraId', as: 'boletos' } }",
        "{ $unwind: '$boletos' }",
        "{ $lookup: { from: 'eventos', localField: 'boletos.eventoId', foreignField: '_id', as: 'evento' } }",
        "{ $unwind: '$evento' }",
        "{ $group: { _id: '$evento._id', evento: { $first: '$evento.nombre' }, " +
        "capacidad: { $first: '$evento.capacidad' }, vendidos: { $sum: 1 } } }",
        "{ $project: { _id: 0, evento: 1, capacidad: 1, vendidos: 1, " +
        "ocupacion: { $multiply: [ { $divide: [ '$vendidos', '$capacidad' ] }, 100 ] } } }",
        "{ $sort: { ocupacion: -1 } }"
    })
    List<OcupacionDto> obtenerOcupacionEventos();

    // Ventas por organizador
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'boletos', localField: '_id', foreignField: 'compraId', as: 'boletos' } }",
        "{ $unwind: '$boletos' }",
        "{ $lookup: { from: 'eventos', localField: 'boletos.eventoId', foreignField: '_id', as: 'evento' } }",
        "{ $unwind: '$evento' }",
        "{ $match: { 'evento.organizadorId': ?0 } }",
        "{ $group: { _id: '$evento._id', evento: { $first: '$evento.nombre' }, " +
        "ventas: { $sum: 1 }, ingresos: { $sum: '$monto' } } }",
        "{ $project: { _id: 0, evento: 1, ventas: 1, ingresos: 1 } }",
        "{ $sort: { ingresos: -1 } }"
    })
    List<VentasEventoDto> obtenerVentasPorOrganizador(Long organizadorId);

    // Asistentes por organizador
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'boletos', localField: '_id', foreignField: 'compraId', as: 'boletos' } }",
        "{ $unwind: '$boletos' }",
        "{ $lookup: { from: 'eventos', localField: 'boletos.eventoId', foreignField: '_id', as: 'evento' } }",
        "{ $unwind: '$evento' }",
        "{ $match: { 'evento.organizadorId': ?0 } }",
        "{ $group: { _id: '$evento._id', evento: { $first: '$evento.nombre' }, " +
        "asistentes: { $sum: 1 } } }",
        "{ $project: { _id: 0, evento: 1, asistentes: 1 } }",
        "{ $sort: { asistentes: -1 } }"
    })
    List<AsistentesDto> obtenerAsistentesPorOrganizador(Long organizadorId);

    // Valoraciones de eventos
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'valoraciones', localField: '_id', foreignField: 'compraId', as: 'valoraciones' } }",
        "{ $unwind: '$valoraciones' }",
        "{ $lookup: { from: 'eventos', localField: 'valoraciones.eventoId', foreignField: '_id', as: 'evento' } }",
        "{ $unwind: '$evento' }",
        "{ $group: { _id: '$evento._id', evento: { $first: '$evento.nombre' }, " +
        "promedio: { $avg: '$valoraciones.puntuacion' }, totalReviews: { $sum: 1 } } }",
        "{ $project: { _id: 0, evento: 1, promedio: 1, totalReviews: 1 } }",
        "{ $sort: { promedio: -1 } }"
    })
    List<ValoracionesDto> obtenerValoracionesEventos();
}
