package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Estado;

@Repository
public interface EstadoRepository extends MongoRepository<Estado, String> {
    Estado findByNombre(String nombre);
}
