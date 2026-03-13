package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NombreEventoDTO {
    private long id;
    private String titulo;

    public NombreEventoDTO(Long id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }
    
    public NombreEventoDTO() {}
}
