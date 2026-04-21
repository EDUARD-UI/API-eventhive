package com.example.demo.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "promociones")
@Getter
@Setter
public class Promocion {

    @Id
    private String id;

    private String nombre;
    private String descripcion;
    private Double descuento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @DBRef(lazy = true)
    @JsonManagedReference
    private List<Evento> eventos;  // Una promoción puede tener MUCHOS eventos
}