package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "estados")
@Getter
@Setter
public class Estado {

    @Id
    private String id;

    @Indexed(unique = true)
    private String nombre;

    private String descripcion;
}
