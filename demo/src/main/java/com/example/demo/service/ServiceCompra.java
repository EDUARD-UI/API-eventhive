package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BoletosCompraDTO;
import com.example.demo.dto.CompraDetalleDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
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

    // funcion para procesar la compra, crear tiquetes y actualizar disponibilidad
    @Transactional
    public Compra procesarCompra(Usuario cliente, String localidadId, String cantidad, String metodoPago) {
        Localidad localidad = localidadRepository.findById(localidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Localidad no encontrada"));

        if (localidad.getDisponibles() < cantidad) {
            throw new BusinessException("No hay suficientes boletos disponibles");
        }

        BigDecimal total = localidad.getPrecio().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : localidad.getPrecio().multiply(new BigDecimal(cantidad));

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

    public Compra obtenerCompraPorId(String compraId) {
        return compraRepository.findById(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada"));
    }

    public Compra obtenerCompraPorIdConDetalles(String compraId) {
        return compraRepository.findByIdWithDetalles(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada"));
    }

    public List<Compra> obtenerComprasPorCliente(String clienteId) {
        return compraRepository.findByClienteIdOrderByFechaCompraDesc(clienteId);
    }

    public List<Compra> obtenerComprasConDetallesPorCliente(String clienteId) {
        return compraRepository.findComprasConDetallesPorClienteId(clienteId);
    }

    public Page<CompraDetalleDTO> obtenerHistorialDTO(String clienteId, Pageable pageable) {
        return compraRepository.findComprasConDetallesPorClienteId(clienteId, pageable)
                .map(this::toDetalleDTO);
    }

    // Historial por cliente
    public List<CompraDetalleDTO> obtenerHistorialDTO(String clienteId) {
        return obtenerComprasConDetallesPorCliente(clienteId).stream()
                .map(this::toDetalleDTO)
                .collect(Collectors.toList());
    }

    // boletos por compra
    public BoletosCompraDTO obtenerBoletosDTO(String compraId) {
        Compra compra = obtenerCompraPorIdConDetalles(compraId);
        if (compra == null) throw new ResourceNotFoundException("La compra no existe");
        return toBoletosDTO(compra);
    }

    // funciones de apoyo de convercion a DTOs
    private CompraDetalleDTO toDetalleDTO(Compra compra) {
        CompraDetalleDTO dto = new CompraDetalleDTO();
        dto.setId(compra.getId());
        dto.setFechaCompra(compra.getFechaCompra());
        dto.setTotal(compra.getTotal());
        dto.setMetodoPago(compra.getMetodoPago());

        if (compra.getTiqueteCompras() == null || compra.getTiqueteCompras().isEmpty()) return dto;

        var primerTiquete = compra.getTiqueteCompras().get(0).getTiquete();
        if (primerTiquete == null || primerTiquete.getLocalidad() == null) return dto;

        var localidad = primerTiquete.getLocalidad();
        dto.setLocalidadNombre(localidad.getNombre());
        dto.setCantidad(compra.getTiqueteCompras().size());

        var evento = localidad.getEvento();
        if (evento != null) {
            dto.setEventoTitulo(evento.getTitulo());
            dto.setEventoFecha(evento.getFecha());
            dto.setEventoHora(evento.getHora());
            dto.setEventoLugar(evento.getLugar());
        }

        return dto;
    }

    private BoletosCompraDTO toBoletosDTO(Compra compra) {
        BoletosCompraDTO dto = new BoletosCompraDTO();
        dto.setId(compra.getId());
        dto.setFechaCompra(compra.getFechaCompra());
        dto.setTotal(compra.getTotal());
        dto.setMetodoPago(compra.getMetodoPago());

        if (compra.getTiqueteCompras() != null) {
            dto.setTiqueteCompras(compra.getTiqueteCompras().stream()
                    .map(this::toBoletoDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private BoletosCompraDTO.BoletoDTO toBoletoDTO(TiqueteCompra tc) {
        BoletosCompraDTO.BoletoDTO b = new BoletosCompraDTO.BoletoDTO();
        b.setId(tc.getId());

        if (tc.getTiquete() == null) return b;

        BoletosCompraDTO.TiqueteDTO t = new BoletosCompraDTO.TiqueteDTO();
        t.setId(tc.getTiquete().getId());
        t.setCodigoQR(tc.getTiquete().getCodigoQR());

        if (tc.getTiquete().getLocalidad() != null) {
            var loc = tc.getTiquete().getLocalidad();
            BoletosCompraDTO.LocalidadDTO l = new BoletosCompraDTO.LocalidadDTO();
            l.setId(loc.getId());
            l.setNombre(loc.getNombre());
            l.setPrecio(loc.getPrecio());

            if (loc.getEvento() != null) {
                var ev = loc.getEvento();
                BoletosCompraDTO.EventoDTO e = new BoletosCompraDTO.EventoDTO();
                e.setId(ev.getId());
                e.setTitulo(ev.getTitulo());
                e.setFecha(ev.getFecha());
                e.setHora(ev.getHora());
                e.setLugar(ev.getLugar());
                l.setEvento(e);
            }
            t.setLocalidad(l);
        }

        b.setTiquete(t);
        return b;
    }
}