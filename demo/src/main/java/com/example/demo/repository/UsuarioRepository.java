package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Usuario;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Usuario findByCorreoAndClave(String correo, String clave);
    Usuario findByCorreo(String correo);
    long countByRolId(String rolId);
    long countByEstadoId(String estadoId);
    
    @Query("{ 'correo': ?0 }")
    Usuario findByCorreoWithRol(String correo);
}
