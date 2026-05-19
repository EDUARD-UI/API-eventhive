package com.example.demo.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Evento;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EventoRepositoryImpl implements EventoRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    private Page<Evento> aggregateWithMatch(Document matchDoc, Pageable pageable) {
        List<AggregationOperation> ops = new ArrayList<>();
        if (matchDoc != null) {
            ops.add(context -> new Document("$match", matchDoc));
        }

        // Add fields converting embedded id strings to ObjectId for lookups
        ops.add(context -> Document.parse("{ $addFields: { categoriaObjectId: { $toObjectId: '$categoria._id' }, estadoObjectId: { $toObjectId: '$estado._id' }, organizadorObjectId: { $toObjectId: '$organizador._id' } } }"));

        // Lookups
        ops.add(context -> Document.parse("{ $lookup: { from: 'categorias', localField: 'categoriaObjectId', foreignField: '_id', as: 'categoriaData' } }"));
        ops.add(context -> Document.parse("{ $unwind: { path: '$categoriaData', preserveNullAndEmptyArrays: true } }"));
        ops.add(context -> Document.parse("{ $lookup: { from: 'estados', localField: 'estadoObjectId', foreignField: '_id', as: 'estadoData' } }"));
        ops.add(context -> Document.parse("{ $unwind: { path: '$estadoData', preserveNullAndEmptyArrays: true } }"));
        ops.add(context -> Document.parse("{ $lookup: { from: 'usuarios', localField: 'organizadorObjectId', foreignField: '_id', as: 'organizadorData' } }"));
        ops.add(context -> Document.parse("{ $unwind: { path: '$organizadorData', preserveNullAndEmptyArrays: true } }"));

        // Replace fields
        ops.add(context -> Document.parse("{ $addFields: { categoria: '$categoriaData', estado: '$estadoData', organizador: '$organizadorData' } }"));

        // Clean auxiliary fields
        ops.add(context -> Document.parse("{ $project: { categoriaData:0, estadoData:0, organizadorData:0, categoriaObjectId:0, estadoObjectId:0, organizadorObjectId:0 } }"));

        // Pagination
        ops.add(context -> Document.parse("{ $skip: " + pageable.getOffset() + " }"));
        ops.add(context -> Document.parse("{ $limit: " + pageable.getPageSize() + " }"));

        Aggregation agg = Aggregation.newAggregation(ops);
        AggregationResults<Evento> results = mongoTemplate.aggregate(agg, "eventos", Evento.class);
        List<Evento> content = results.getMappedResults();

        long total;
        if (matchDoc == null) {
            total = mongoTemplate.getCollection("eventos").countDocuments();
        } else {
            total = mongoTemplate.getCollection("eventos").countDocuments(matchDoc);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Evento> findAllWithReferences(Pageable pageable) {
        return aggregateWithMatch(null, pageable);
    }

    @Override
    public Page<Evento> findByOrganizadorIdWithReferences(String organizadorId, Pageable pageable) {
        Document match = new Document("organizador._id", organizadorId);
        return aggregateWithMatch(match, pageable);
    }

    @Override
    public Page<Evento> findByOrganizadorIdAndTituloContainingIgnoreCaseWithReferences(String organizadorId,
            String titulo, Pageable pageable) {
        Document match = new Document();
        match.put("organizador._id", organizadorId);
        match.put("titulo", new Document("$regex", titulo).append("$options", "i"));
        return aggregateWithMatch(match, pageable);
    }

    @Override
    public Page<Evento> findByTituloContainingIgnoreCaseWithReferences(String titulo, Pageable pageable) {
        Document match = new Document("titulo", new Document("$regex", titulo).append("$options", "i"));
        return aggregateWithMatch(match, pageable);
    }

    @Override
    public Page<Evento> findByCategoriaIdWithReferences(String categoriaId, Pageable pageable) {
        Document match = new Document("categoria._id", categoriaId);
        return aggregateWithMatch(match, pageable);
    }

    @Override
    public Page<Evento> findByEstadoIdWithReferences(String estadoId, Pageable pageable) {
        Document match = new Document("estado._id", estadoId);
        return aggregateWithMatch(match, pageable);
    }

    @Override
    public Evento findByIdWithReferences(String id) {
        try {
            Document match = new Document("_id", new ObjectId(id));
            Page<Evento> p = aggregateWithMatch(match, Pageable.ofSize(1));
            if (p.hasContent()) {
                return p.getContent().get(0);
            }
            return null;
        } catch (IllegalArgumentException e) {
            // Not a valid ObjectId; try matching by string id field
            Document match = new Document("_id", id);
            Page<Evento> p = aggregateWithMatch(match, Pageable.ofSize(1));
            if (p.hasContent()) {
                return p.getContent().get(0);
            }
            return null;
        }
    }

}
