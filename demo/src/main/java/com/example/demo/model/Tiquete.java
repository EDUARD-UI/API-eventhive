package com.example.demo.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "tiquetes")
@Getter
@Setter
public class Tiquete {

    @Id
    private String id;

    @Indexed(unique = true)
    private String codigoQR;

    @DBRef
    private Localidad localidad;

    @JsonIgnore
    @DBRef
    private List<TiqueteCompra> tiqueteCompras;
}