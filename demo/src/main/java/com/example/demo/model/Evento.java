package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "eventos")
@Getter
@Setter
public class Evento {

    @Id
    private String id;

    private String titulo;
    private String descripcion;
    private String foto;
    private LocalDate fecha;
    private LocalTime hora;
    private String lugar;

    @DBRef(lazy = true)
    private Estado estado;

    @DBRef(lazy = true)
    private Categoria categoria;

    @DBRef(lazy = true)
    private Usuario organizador;

    private List<Localidad> localidades;

    @DBRef(lazy = true)
    private List<Promocion> promociones;

    @DBRef(lazy = true)
    private List<Valoracion> valoraciones;
}