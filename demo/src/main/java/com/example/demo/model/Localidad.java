package com.example.demo.model;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "localidades")
@Getter
@Setter
public class Localidad {

    @Id
    private String id;

    private String nombre;

    private BigDecimal precio;

    private Integer capacidad;

    private Integer disponibles;

    @JsonIgnore
    @DBRef
    private Evento evento;

    @JsonIgnore
    @DBRef
    private List<Tiquete> tiquetes;
}