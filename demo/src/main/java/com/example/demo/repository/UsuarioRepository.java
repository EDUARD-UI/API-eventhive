package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByCorreoAndClave(String correo, String clave);
    Usuario findByCorreo(String correo);
    long countByRolId(Long rolId);
    Long countByEstadoId(Long estadoId);
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.rol LEFT JOIN FETCH u.estado WHERE u.correo = :correo")
    Usuario findByCorreoWithRol(@Param("correo") String correo);
}
