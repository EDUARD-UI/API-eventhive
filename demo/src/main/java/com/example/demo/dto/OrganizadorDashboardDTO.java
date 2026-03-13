// demo/src/main/java/com/example/demo/dto/OrganizadorDashboardDTO.java
package com.example.demo.dto;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizadorDashboardDTO {
    private int totalEventos;
    private int totalLocalidades;
    private List<EventoResumenDTO> proximosEventos;
    private List<NombreEventoDTO> nombresEventos;

    @Getter @Setter
    public static class EventoResumenDTO {
        private Long id;
        private String titulo;
        private String lugar;
        private String fecha;
        private String hora;
        private String categoriaNombre;
        private String estadoNombre;
        private String foto;
    }
}