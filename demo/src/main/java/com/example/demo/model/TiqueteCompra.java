package com.example.demo.model;

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
@Table(name = "Tiquete_Compra")
public class TiqueteCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTiquete_Compra")
    private Integer id;

    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "idTiquete", nullable = false)
    private Tiquete tiquete;

    @ManyToOne
    @JoinColumn(name = "idCompra", nullable = false)
    private Compra compra;
}