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
public class ValoracionesDto {
    
    @JsonProperty("evento")
    private String evento;
    
    @JsonProperty("promedio")
    private BigDecimal promedio;
    
    @JsonProperty("totalReviews")
    private Integer totalReviews;
}
