package com.example.demo.dto.reportes;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngresosCategoriaDto {
    private String categoria;
    private BigDecimal ingresos;
    private Integer totalEventos;
}