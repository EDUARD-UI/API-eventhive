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
public class CompradoresFrecuentesDto {
    private String clienteId;
    private String nombre;
    private Integer totalCompras;
    private BigDecimal totalGastado;
}