package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.demo.model.Rol;

public interface RolesRepository extends MongoRepository<Rol, String> {

    /**
     * Buscar rol por nombre (exacto, sensible a mayúsculas)
     * IMPORTANTE: Los roles en MongoDB están en MAYÚSCULAS: CLIENTE, ORGANIZADOR, ADMINISTRADOR
     * 
     * @param nombre nombre del rol (ej: "CLIENTE", "ORGANIZADOR", "ADMINISTRADOR")
     * @return Rol encontrado o null
     */
    @Query("{ 'nombre': ?0 }")
    Rol findByNombre(String nombre);

    /**
     * Verificar si existe un rol con ese nombre
     * @param nombre nombre del rol
     * @return true si existe
     */
    boolean existsByNombre(String nombre);
}
