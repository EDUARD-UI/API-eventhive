package com.example.demo.controllers;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.example.demo.service.ServicePromocion;
import com.example.demo.utils.AuthenticatedUserHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promociones")
public class PromocionApiController {

    private final ServicePromocion servicePromocion;
    private final AuthenticatedUserHelper authHelper;

    @GetMapping("/organizador")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Page<PromocionDTO>>> porOrganizador(Pageable pageable) {
        Usuario usuario = authHelper.usuarioAutenticado();
        return ResponseEntity.ok(ApiResponse.ok("Promociones obtenidas",
            servicePromocion.obtenerDTOPorOrganizador(usuario.getId(), pageable)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Void>> crear(
            @RequestParam String eventoId,
            @RequestParam String descripcion,
            @RequestParam BigDecimal descuento,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        Usuario usuario = authHelper.usuarioAutenticado();
        servicePromocion.crearPromocion(eventoId, descripcion, descuento, fechaInicio, fechaFin, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Promoción creada"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Void>> actualizar(
            @PathVariable String id,
            @RequestParam String eventoId,
            @RequestParam String descripcion,
            @RequestParam BigDecimal descuento,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        Usuario usuario = authHelper.usuarioAutenticado();
        servicePromocion.actualizarPromocion(id, eventoId, descripcion, descuento, fechaInicio, fechaFin, usuario);
        return ResponseEntity.ok(ApiResponse.ok("Promoción actualizada"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String id) {
        Usuario usuario = authHelper.usuarioAutenticado();
        servicePromocion.eliminarPromocion(id, usuario);
        return ResponseEntity.ok(ApiResponse.ok("Promoción eliminada"));
    }
}