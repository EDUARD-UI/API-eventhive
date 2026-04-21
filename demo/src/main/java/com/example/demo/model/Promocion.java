package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "promociones")
@Getter
@Setter
public class Promocion {

    @Id
    private String id;

    private String descripcion;

    private BigDecimal descuento;

    private LocalDate fechaInicio;

    private LocalDate fechaFinal;

    @DBRef
    private Evento evento;

    @JsonIgnore
    @DBRef
    private Usuario organizador;
}
