package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "eventosDeseados")
@Getter
@Setter
public class EventoDeseado {

    @Id
    private String id;

    @JsonIgnore
    @DBRef
    private Usuario usuario;

    @DBRef
    private Evento evento;
}