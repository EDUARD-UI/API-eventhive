package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoDeseadoDTO {

    private Long id;
    private Long eventoId;
    private String eventoTitulo;
    private String eventoLugar;
    private String eventoFoto;
}
