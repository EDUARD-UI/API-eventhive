package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Tiquete;

@Repository
public interface TiqueteRepository extends MongoRepository<Tiquete, String> {

    // Retorna todos los tiquetes de una compra dada
    List<Tiquete> findByCompraId(String compraId);
}
