package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private String localidadId;
    @DBRef
    private Evento evento;

    @DBRef
    private Compra compra;
}
