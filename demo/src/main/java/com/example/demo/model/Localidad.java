package com.example.demo.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Localidad {
    private String id;
    private String nombre;
    private BigDecimal precio;
    private Integer capacidad;
    private Integer disponibles;
}