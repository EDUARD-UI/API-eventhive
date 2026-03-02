package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Compra;
import com.example.demo.model.Localidad;
import com.example.demo.model.Tiquete;
import com.example.demo.model.TiqueteCompra;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CompraRepository;
import com.example.demo.repository.LocalidadRepository;
import com.example.demo.repository.TiqueteCompraRepository;
import com.example.demo.repository.TiqueteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceCompra {

    private final CompraRepository compraRepository;
    private final TiqueteRepository tiqueteRepository;
    private final TiqueteCompraRepository tiqueteCompraRepository;
    private final LocalidadRepository localidadRepository;

    @Transactional
    public Compra procesarCompra(Usuario cliente, Long localidadId, Integer cantidad, String metodoPago) {
        Localidad localidad = localidadRepository.findById(localidadId)
                .orElseThrow(() -> new RuntimeException("Localidad no encontrada"));

        if (localidad.getDisponibles() < cantidad) {
            throw new RuntimeException("No hay suficientes boletos disponibles");
        }

        // Calcular total - manejar caso gratuito
        BigDecimal total;
        if (localidad.getPrecio().compareTo(BigDecimal.ZERO) == 0) {
            total = BigDecimal.ZERO;
        } else {
            total = localidad.getPrecio().multiply(new BigDecimal(cantidad));
        }

        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setFechaCompra(LocalDateTime.now());
        compra.setTotal(total);
        compra.setMetodoPago(metodoPago);
        compra = compraRepository.save(compra);

        for (int i = 0; i < cantidad; i++) {
            Tiquete tiquete = new Tiquete();
            tiquete.setCodigoQR(UUID.randomUUID().toString());
            tiquete.setLocalidad(localidad);
            tiquete = tiqueteRepository.save(tiquete);

            TiqueteCompra tiqueteCompra = new TiqueteCompra();
            tiqueteCompra.setTiquete(tiquete);
            tiqueteCompra.setCompra(compra);
            tiqueteCompra.setCantidad(1);
            tiqueteCompraRepository.save(tiqueteCompra);
        }

        localidad.setDisponibles(localidad.getDisponibles() - cantidad);
        localidadRepository.save(localidad);

        return compra;
    }

    public List<Compra> obtenerComprasPorCliente(Long clienteId) {
        return compraRepository.findByClienteIdOrderByFechaCompraDesc(clienteId);
    }

    public List<Compra> obtenerComprasConDetallesPorCliente(Long clienteId) {
        return compraRepository.findComprasConDetallesPorClienteId(clienteId);
    }

    public Compra obtenerCompraPorId(Integer compraId) {
        return compraRepository.findById(compraId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
    }

    public Compra obtenerCompraPorIdConDetalles(Integer compraId) {
        return compraRepository.findByIdWithDetalles(compraId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
    }
}