package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "valoraciones")
@Getter
@Setter
public class Valoracion {

    @Id
    private String id;

    private String comentario;

    private Integer calificacion;

    @JsonIgnore
    @DBRef
    private Usuario cliente;

    @JsonIgnore
    @DBRef
    private Evento evento;
}