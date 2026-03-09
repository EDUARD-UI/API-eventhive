package com.example.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name= "estados")
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEstado")
    private long id;

    @Column(length = 45)
    private String nombre;

    @Column(length = 60)
    private String descripcion;

    @JsonIgnore
    @OneToMany(mappedBy = "estado")
    private List<Usuario> usuarios;

    @JsonIgnore
    @OneToMany(mappedBy = "estado")
    private List<Rol> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "estado")
    private List<Evento> eventos;
}