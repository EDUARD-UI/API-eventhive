package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.SolicitudVerificacion;

@Repository
public interface SolicitudVerificacionRepository extends MongoRepository<SolicitudVerificacion, String> {

    // Obtener solicitud por organizador
    SolicitudVerificacion findByOrganizadorId(String organizadorId);

    // Listar todas las solicitudes pendientes
    List<SolicitudVerificacion> findByEstado(String estado);

    // Listar solicitudes pendientes con paginación
    Page<SolicitudVerificacion> findByEstado(String estado, Pageable pageable);

    // Listar todas las solicitudes de un organizador
    List<SolicitudVerificacion> findByOrganizadorIdOrderByFechaSolicitudDesc(String organizadorId);
}
