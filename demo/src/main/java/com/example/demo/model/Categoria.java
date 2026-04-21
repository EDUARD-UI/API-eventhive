package com.example.demo.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "categorias")
@Getter
@Setter
public class Categoria {

    @Id
    private String id;

    private String foto;

    @Indexed(unique = true)
    private String nombre;

    @JsonIgnore
    @DBRef(lazy = true)
    private List<Evento> eventos;
}
