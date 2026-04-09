package com.example.demo.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Localidades")
public class Localidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLocalidades")
    private Long  id;

    @Column(length = 45)
    private String nombre;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    private Integer capacidad;

    private Integer disponibles;

    @ManyToOne
    @JsonIgnore 
    @JoinColumn(name = "idEvento", nullable = false)
    private Evento evento;

    @OneToMany(mappedBy = "localidad", cascade = CascadeType.ALL)
    @JsonIgnore 
    private List<Tiquete> tiquetes;
}