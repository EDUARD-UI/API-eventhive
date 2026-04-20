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

    private Rol rol;

    @JsonIgnore
    @DBRef(lazy = true)
    private List<Evento> eventosOrganizados;

    private List<String> eventosDeseadosIds;

    @JsonIgnore
    @DBRef(lazy = true)
    private List<Valoracion> valoraciones;

    @JsonIgnore
    @DBRef(lazy = true)
    private List<Compra> compras;
}