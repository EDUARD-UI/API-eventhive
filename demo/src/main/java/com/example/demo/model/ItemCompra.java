package com.example.demo.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCompra {
    private String eventoId;
    private String localidadId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}