package com.example.demo.dto.reportes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcupacionDto {
    
    @JsonProperty("evento")
    private String evento;
    
    @JsonProperty("capacidad")
    private Integer capacidad;
    
    @JsonProperty("vendidos")
    private Integer vendidos;
    
    @JsonProperty("ocupacion")
    private BigDecimal ocupacion;
}
