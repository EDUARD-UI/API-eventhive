package com.example.demo.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoDTO {
    Long id;
    String titulo, descripcion, lugar, foto;
    LocalDate fecha;

    public EventoDTO() {
    }

    public EventoDTO(Long id, String titulo, String descripcion, String lugar, String foto, LocalDate fecha) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.foto = foto;
        this.fecha = fecha;
    }
    
}
