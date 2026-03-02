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
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Long id;
    
    @Column(length = 45)
    private String nombre;
    
    @Column(length = 45)
    private String apellido;
    
    @Column(length = 45, unique = true)
    private String correo;
    
    @Column(length = 45)
    private String telefono;

    @Column(length = 45)
    private String clave;
    
    @ManyToOne
    @JoinColumn(name = "idEstado", nullable = false)
    private Estado estado;
    
    @ManyToOne
    @JoinColumn(name = "idRoles", nullable = false)
    private Rol rol;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Evento> eventosOrganizados;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<EventoDeseado> eventosDeseados;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Valoracion> valoraciones;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Compra> compras;
}