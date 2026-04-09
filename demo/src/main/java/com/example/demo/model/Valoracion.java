package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Valoracion")
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idValoracion")
    private Long id;

    @Column(length = 200)
    private String comentario;

    private Long calificacion;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idCliente", nullable = false)
    private Usuario cliente;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idEvento", nullable = false)
    private Evento evento;
}