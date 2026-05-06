package com.example.demo.dto.reportes;

import org.springframework.data.mongodb.core.mapping.Field;
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
    
    @Field("_id")  // ← Cambiado de "categoria" a "_id"
    @JsonProperty("categoria")
    private String categoria;
    
    @Field("total")
    @JsonProperty("total")
    private Integer total;
}