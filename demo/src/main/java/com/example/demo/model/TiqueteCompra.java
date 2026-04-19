package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "tiqueteCompras")
@Getter
@Setter
public class TiqueteCompra {

    @Id
    private String id;

    private Integer cantidad;

    @DBRef
    private Tiquete tiquete;

    @JsonIgnore
    @DBRef
    private Compra compra;
}