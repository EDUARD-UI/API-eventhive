package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.reportes.EventosCategoriaDto;
import com.example.demo.model.Tiquete;

@Repository
public interface TiqueteRepository extends MongoRepository<Tiquete, String> {

    @Aggregation(pipeline = {
    "{ $lookup: { from: 'eventos', localField: 'evento._id', foreignField: '_id', as: 'eventoData' } }",  // Cambiar $evento.$id por $evento._id
    "{ $unwind: { path: '$eventoData', preserveNullAndEmptyArrays: false } }",
    "{ $lookup: { from: 'categorias', localField: 'eventoData.categoria._id', foreignField: '_id', as: 'categData' } }",  // Cambiar $eventoData.categoria.$id por $eventoData.categoria._id
    "{ $unwind: { path: '$categData', preserveNullAndEmptyArrays: true } }",
    "{ $group: { _id: '$categData.nombre', total: { $sum: 1 } } }",
    "{ $match: { _id: { $ne: null } } }",
    "{ $project: { _id: 0, categoria: '$_id', total: 1 } }",
    "{ $sort: { total: -1 } }"
})
List<EventosCategoriaDto> obtenerEventosPorCategoria();
}
