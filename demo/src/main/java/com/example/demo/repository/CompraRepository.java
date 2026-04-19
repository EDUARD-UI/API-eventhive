package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {

    List<Compra> findByClienteIdOrderByFechaCompraDesc(Long clienteId);

    @Query("SELECT c FROM Compra c " +
       "LEFT JOIN FETCH c.tiqueteCompras tc " +
       "LEFT JOIN FETCH tc.tiquete t " +
       "LEFT JOIN FETCH t.localidad l " +
       "LEFT JOIN FETCH l.evento e " +
       "WHERE c.cliente.id = :clienteId " +
       "ORDER BY c.fechaCompra DESC")
        Page<Compra> findComprasConDetallesPorClienteId(@Param("clienteId") Long clienteId, Pageable pageable);

    @Query("SELECT c FROM Compra c "
            + "LEFT JOIN FETCH c.tiqueteCompras tc "
            + "LEFT JOIN FETCH tc.tiquete t "
            + "LEFT JOIN FETCH t.localidad l "
            + "LEFT JOIN FETCH l.evento e "
            + "WHERE c.cliente.id = :clienteId "
            + "ORDER BY c.fechaCompra DESC")
    List<Compra> findComprasConDetallesPorClienteId(@Param("clienteId") Long clienteId);
    
    //query para obtener una compra específica con todos sus detalles
    @Query("SELECT c FROM Compra c "
            + "LEFT JOIN FETCH c.tiqueteCompras tc "
            + "LEFT JOIN FETCH tc.tiquete t "
            + "LEFT JOIN FETCH t.localidad l "
            + "LEFT JOIN FETCH l.evento e "
            + "WHERE c.id = :compraId")
    Optional<Compra> findByIdWithDetalles(@Param("compraId") Integer compraId);
}