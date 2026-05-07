package com.example.demo.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Localidad {
    private String id;
    private String nombre;
    private BigDecimal precio;
    private Integer capacidad;
    private Integer disponibles;
}