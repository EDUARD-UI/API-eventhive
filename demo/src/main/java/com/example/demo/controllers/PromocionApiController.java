package com.example.demo.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PromocionDTO;
import com.example.demo.model.Usuario;
import com.example.demo.security.SecurityController;
import com.example.demo.service.ServicePromocion;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promociones")
public class PromocionApiController {

    private final ServicePromocion servicePromocion;
    private final SecurityController securityController;

    @GetMapping("/organizador")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<List<PromocionDTO>>> listarPorOrganizador() {
        return ResponseEntity.ok(ApiResponse.ok("Promociones obtenidas",
                servicePromocion.obtenerDTOPorOrganizador(usuarioAutenticado().getId())));
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Void>> crear(
            @RequestParam Long eventoId,
            @RequestParam String descripcion,
            @RequestParam BigDecimal descuento,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {

        servicePromocion.crearPromocion(eventoId, descripcion, descuento,
                fechaInicio, fechaFin, usuarioAutenticado());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Promoción creada exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Void>> actualizar(
            @PathVariable Long id,
            @RequestParam Long eventoId,
            @RequestParam String descripcion,
            @RequestParam BigDecimal descuento,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {

        servicePromocion.actualizarPromocion(id, eventoId, descripcion, descuento,
                fechaInicio, fechaFin, usuarioAutenticado());
        return ResponseEntity.ok(ApiResponse.ok("Promoción actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        servicePromocion.eliminarPromocion(id, usuarioAutenticado());
        return ResponseEntity.ok(ApiResponse.ok("Promoción eliminada exitosamente"));
    }

    private Usuario usuarioAutenticado() {
        return securityController.usuarioAutenticado();
    }
}