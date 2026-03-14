package com.example.demo.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrganizadorDashboardDTO {
    private int totalEventos;
    private int totalLocalidades;
    private List<NombreEventoDTO> nombresEventos;
}