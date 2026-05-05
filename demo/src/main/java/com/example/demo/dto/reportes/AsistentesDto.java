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
public class AsistentesDto {
    
    @JsonProperty("evento")
    private String evento;
    
    @JsonProperty("asistentes")
    private Integer asistentes;
}
