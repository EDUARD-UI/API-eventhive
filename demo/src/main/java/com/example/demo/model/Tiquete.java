package com.example.demo.model;

import java.util.List;

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
@Table(name = "Tiquete")
public class Tiquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTiquete")
    private Integer id;

    @Column(length = 45, unique = true)
    private String codigoQR;

    @ManyToOne
    @JoinColumn(name = "idLocalidades", nullable = false)
    private Localidad localidad;

    @OneToMany(mappedBy = "tiquete", cascade = CascadeType.ALL)
    private List<TiqueteCompra> tiqueteCompras;
}