package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.demo.model.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    /**
     * Buscar usuario por correo (exacto)
     * @param correo correo único del usuario
     * @return Usuario encontrado o null
     */
    @Query("{ 'correo': ?0 }")
    Usuario findByCorreo(String correo);

    /**
     * Contar usuarios con un rol específico
     * @param rolId ID del rol
     * @return cantidad de usuarios
     */
    @Query("{ 'rol.$id': ?0 }")
    long countByRolId(String rolId);

    /**
     * Verificar si existe un usuario con ese correo
     * @param correo correo a verificar
     * @return true si existe
     */
    boolean existsByCorreo(String correo);
}
