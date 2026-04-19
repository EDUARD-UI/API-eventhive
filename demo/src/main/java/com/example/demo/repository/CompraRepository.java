package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Compra;

@Repository
public interface CompraRepository extends MongoRepository<Compra, String> {

    List<Compra> findByClienteIdOrderByFechaCompraDesc(String clienteId);

    Page<Compra> findByClienteIdOrderByFechaCompraDesc(String clienteId, Pageable pageable);

    @Override
    Optional<Compra> findById(String compraId);
}