package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Usuario;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Usuario findByCorreo(String correo);
    
    @Override
    Optional<Usuario> findById(String id);

    boolean existsByCorreo(String correo);

    long countByRolId(String rolId);
}