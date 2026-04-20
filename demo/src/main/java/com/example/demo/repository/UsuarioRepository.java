package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.demo.model.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    @Query("{ 'correo': ?0 }")
    Usuario findByCorreo(String correo);

    @Query("{ 'rol.$id': ?0 }")
    long countByRolId(String rolId);

    boolean existsByCorreo(String correo);
}
