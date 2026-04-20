package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.demo.model.Rol;

public interface RolesRepository extends MongoRepository<Rol, String> {

    @Query("{ 'nombre': ?0 }")
    Rol findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
