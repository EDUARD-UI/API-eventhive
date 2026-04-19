package com.example.demo.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "usuarios")
@Getter
@Setter
public class Usuario {

    @Id
    private String id;

    private String nombre;

    private String apellido;

    @Indexed(unique = true)
    private String correo;

    private String telefono;

    @JsonIgnore
    private String clave;

    @DBRef
    private Rol rol;

    @JsonIgnore
    @DBRef
    private List<Evento> eventosOrganizados;

    @JsonIgnore
    @DBRef
    private List<EventoDeseado> eventosDeseados;

    @JsonIgnore
    @DBRef
    private List<Valoracion> valoraciones;

    @JsonIgnore
    @DBRef
    private List<Compra> compras;
}