package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValoracionDTO {

    private long id;
    private String comentario;
    private long calificacion;
    private Long eventoId;
    private String eventoTitulo;
}
