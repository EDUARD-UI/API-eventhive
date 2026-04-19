package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BoletosCompraDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Compra;
import com.example.demo.model.Usuario;
import com.example.demo.service.ServiceCompra;
import com.example.demo.utils.AuthenticatedUserHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boletos")
public class BoletosApiController {

    private final ServiceCompra serviceCompra;
    private final AuthenticatedUserHelper authHelper;

    @GetMapping("/{compraId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<BoletosCompraDTO>> verBoletos(@PathVariable String compraId) {
        Usuario usuario = authHelper.usuarioAutenticado();

        Compra compra = serviceCompra.obtenerCompraPorId(compraId);
        if (!compra.getCliente().getId().equals(usuario.getId()))
            throw new BusinessException("No autorizado para ver esta compra");

        return ResponseEntity.ok(ApiResponse.ok("Compra obtenida", serviceCompra.obtenerBoletosDTO(compraId)));
    }
}
