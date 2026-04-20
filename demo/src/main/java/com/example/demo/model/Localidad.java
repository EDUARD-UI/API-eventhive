package com.example.demo.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

// ⚠️ SIN @Document - Esta clase es EMBEBIDA dentro de Evento
@Getter
@Setter
public class Localidad {
    private String id;  // Opcional
    private String nombre;
    private BigDecimal precio;
    private Integer capacidad;
    private Integer disponibles;
}