package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/compras")
public class ComprasApiController {

    private final ServiceCompra serviceCompra;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> procesarCompra(
            @RequestParam Long localidadId,
            @RequestParam Integer cantidad,
            @RequestParam String metodoPago,
            HttpSession session) {

        Compra compra = serviceCompra.procesarCompra(getUsuarioSesion(session), localidadId, cantidad, metodoPago);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Compra realizada exitosamente", Map.of("compraId", compra.getId())));
    }

    @GetMapping("/historial")
    public ResponseEntity<ApiResponse<List<CompraDetalleDTO>>> historialCompras(HttpSession session) {
        List<CompraDetalleDTO> dtos = serviceCompra.obtenerHistorialDTO(getUsuarioSesion(session).getId());
        return ResponseEntity.ok(ApiResponse.ok("Historial de compras", dtos));
    }

    @GetMapping("/{compraId}")
    public ResponseEntity<ApiResponse<Compra>> obtenerCompra(
            @PathVariable Integer compraId,
            HttpSession session) {

        Usuario usuario = getUsuarioSesion(session);
        Compra compra   = serviceCompra.obtenerCompraPorIdConDetalles(compraId);

        if (!compra.getCliente().getId().equals(usuario.getId())) {
            throw new BusinessException("No tiene permiso para ver esta compra");
        }

        return ResponseEntity.ok(ApiResponse.ok("Compra obtenida", compra));
    }

    // FUNCIONES DE APOYO

    private Usuario getUsuarioSesion(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuario == null) throw new BusinessException("Debe iniciar sesión para realizar esta acción");
        return usuario;
    }
}