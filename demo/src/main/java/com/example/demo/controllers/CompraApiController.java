package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Compra;
import com.example.demo.model.ItemCompra;
import com.example.demo.service.ServiceCompra;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraApiController {

    private final ServiceCompra compraService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Compra>>> listarMisCompras() {
        return ResponseEntity.ok(ApiResponse.ok(compraService.listarMisCompras()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Compra>> obtenerCompra(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(compraService.obtenerPorId(id)));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Compra>> realizarCompra(@RequestBody List<ItemCompra> items) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(compraService.realizarCompra(items)));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> cancelarCompra(@PathVariable String id) {
        compraService.cancelarCompra(id);
        return ResponseEntity.ok(ApiResponse.ok("Compra cancelada"));
    }
}