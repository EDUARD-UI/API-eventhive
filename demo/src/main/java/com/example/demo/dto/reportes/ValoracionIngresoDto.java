package com.example.demo.dto.reportes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValoracionIngresoDto {
    private String evento;
    private Double promedioCalificacion;
    private Integer totalReviews;
    private BigDecimal ingresos;
}