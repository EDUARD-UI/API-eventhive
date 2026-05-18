package com.example.demo.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class Localidad {

    @Id
    private String id;

    private String nombre;
    private BigDecimal precio;
    private int capacidad;
    private int disponibles;
}
