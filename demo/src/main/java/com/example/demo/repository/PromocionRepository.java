package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Promocion;

@Repository
public interface PromocionRepository extends MongoRepository<Promocion, String> {

    // Sin lazy loading, MongoDB guarda DBRef como { "$ref": "eventos", "$id": ObjectId("...") }
    // La query correcta usa $id (no _id)
    @Query("{ 'eventos.$id': { $oid: ?0 } }")
    List<Promocion> findByEventosId(String eventoId);

    @Query("{ 'eventos.$id': { $oid: ?0 } }")
    Promocion findFirstByEventosId(String eventoId);

    @Query("{ 'eventos.$id': { $in: ?0 } }")
    Page<Promocion> findByEventosIdIn(List<String> eventoIds, Pageable pageable);
}