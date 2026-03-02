package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Rol;

@Repository
public interface RolesRepository extends JpaRepository<Rol, Long> {
    Rol findByNombre(String nombre);

}
