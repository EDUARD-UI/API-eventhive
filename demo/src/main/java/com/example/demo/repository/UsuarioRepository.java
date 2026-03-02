package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByCorreoAndClave(String correo, String clave);
    Usuario findByCorreo(String correo);
    long countByRolId(Long rolId);
    Long countByEstadoId(Long estadoId);
}
