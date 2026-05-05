package com.example.demo.dto.reportes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentasFechaDto {
    
    @JsonProperty("fecha")
    private LocalDate fecha;
    
    @JsonProperty("totalVentas")
    private Integer totalVentas;
    
    @JsonProperty("ingresos")
    private BigDecimal ingresos;
}
