package com.example.demo.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "roles")
@Getter
@Setter
public class Rol {

    @Id
    private String id;

    private String nombre;
    private String descripcion;

    @JsonIgnore
    @DBRef(lazy = true)
    private List<Usuario> usuarios;
}