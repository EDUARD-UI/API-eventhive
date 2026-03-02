package com.example.demo.dto;

public class EventoBusquedaDTO {
    private Long id;
    private String titulo, nombreCategoria;

    public EventoBusquedaDTO(Long id, String titulo, String nombreCategoria) {
        this.id = id;
        this.titulo = titulo;
        this.nombreCategoria = nombreCategoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    
}
