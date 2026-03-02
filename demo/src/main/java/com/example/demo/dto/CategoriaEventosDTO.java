package com.example.demo.dto;

import java.util.List;

public class CategoriaEventosDTO {
    private Long id;
    private String nombre;
    private List<EventoDTO> eventos;
    private Long totalEventos;
    
    public Long getTotalEventos() {
        return totalEventos;
    }
    
    public void setTotalEventos(Long totalEventos) {
        this.totalEventos = totalEventos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<EventoDTO> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoDTO> eventos) {
        this.eventos = eventos;
    }

    
}
