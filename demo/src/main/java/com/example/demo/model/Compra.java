package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "compras")
@Getter
@Setter
public class Compra {

    @Id
    private String id;

    private LocalDateTime fechaCompra;
    private BigDecimal total;
    private String metodoPago;

    @JsonIgnore
    @DBRef
    private Usuario cliente;

    private List<ItemCompra> items;
}
