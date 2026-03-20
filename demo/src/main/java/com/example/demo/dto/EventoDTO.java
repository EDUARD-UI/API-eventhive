package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoDTO {
    Long id;
    String titulo, descripcion, lugar, foto;
    LocalDate fecha;
    LocalTime hora;
    Categoria categoria;
    Estado estado;

    @Getter
    @Setter
    public static class Categoria {
        Long id;
        String nombre;

        public Categoria(Long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
    }

    @Getter
    @Setter
    public static class Estado {
        Long id;
        String nombre;

        public Estado(Long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
    }

    public EventoDTO() {
    }

    public EventoDTO(Long id, String titulo, String descripcion, String lugar, String foto, LocalDate fecha, LocalTime hora, 
                     Categoria categoria, Estado estado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.foto = foto;
        this.fecha = fecha;
        this.hora = hora;
        this.categoria = categoria;
        this.estado = estado;
    }
    
}
