package com.example.demo.controllers;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CompraDetalleDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Compra;
import com.example.demo.model.Usuario;
import com.example.demo.service.ServiceCompra;
import com.example.demo.utils.AuthenticatedUserHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/compras")
public class ComprasApiController {

    private final ServiceCompra serviceCompra;
    private final AuthenticatedUserHelper authHelper;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> procesarCompra(
            @RequestParam Long localidadId,
            @RequestParam Integer cantidad,
            @RequestParam String metodoPago) {

        Compra compra = serviceCompra.procesarCompra(authHelper.usuarioAutenticado(), localidadId, cantidad, metodoPago);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Compra realizada exitosamente", Map.of("compraId", compra.getId())));
    }

    @GetMapping("/historial")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<Page<CompraDetalleDTO>>> historialCompras(Pageable pageable) {
        Page<CompraDetalleDTO> dtos = serviceCompra.obtenerHistorialDTO(authHelper.usuarioAutenticado().getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("Historial de compras", dtos));
    }

    @GetMapping("/{compraId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<Compra>> obtenerCompra(@PathVariable Integer compraId) {
        Usuario usuario = authHelper.usuarioAutenticado();
        Compra compra = serviceCompra.obtenerCompraPorIdConDetalles(compraId);

        if (!compra.getCliente().getId().equals(usuario.getId()))
            throw new BusinessException("No tiene permiso para ver esta compra");

        return ResponseEntity.ok(ApiResponse.ok("Compra obtenida", compra));
    }
}