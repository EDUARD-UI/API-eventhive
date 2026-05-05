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
public class UsuariosRolDto {
    
    @JsonProperty("rol")
    private String rol;
    
    @JsonProperty("cantidad")
    private Integer cantidad;
}
