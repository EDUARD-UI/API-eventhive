package com.example.demo.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "solicitudes_verificacion")
@Getter
@Setter
public class SolicitudVerificacion {

    @Id
    private String id;

    @DBRef
    private Usuario organizador;

    private String mensaje;

    private String archivoConfirmacion;

    private String estado = "PENDIENTE"; // PENDIENTE, APROBADA, RECHAZADA

    private LocalDateTime fechaSolicitud;

    private LocalDateTime fechaResolucion;

    @JsonIgnore
    @DBRef
    private Usuario administradorQueResolvi;

    private String motivoRechazo;
}
