package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Categoria;

@Repository
public interface CategoriasRepository extends MongoRepository<Categoria, String> {
    List<Categoria> findTop4ByOrderByNombreAsc();
    Categoria findByNombre(String nombre);
}
