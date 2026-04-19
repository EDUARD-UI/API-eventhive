package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @DBRef
    private Estado estado;

    @DBRef
    private Categoria categoria;

    @DBRef
    private Usuario usuario;

    @JsonIgnore
    @DBRef
    private List<Localidad> localidades;

    @JsonIgnore
    @DBRef
    private List<Promocion> promociones;

    @JsonIgnore
    @DBRef
    private List<Valoracion> valoraciones;

    @JsonIgnore
    @DBRef
    private List<EventoDeseado> eventosDeseados;
}
