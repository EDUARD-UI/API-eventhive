package com.example.demo.dto.reportes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventosCategoriaDto {
    
    @JsonProperty("categoria")
    private String categoria;
    
    @JsonProperty("total")
    private Integer total;
}
