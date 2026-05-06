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
public class MetodoPagoDto {
    private String metodo;
    private Integer cantidad;
    private BigDecimal totalRecaudado;
}