package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
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

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boletos")
public class BoletosApiController {

    private final ServiceCompra serviceCompra;

    @GetMapping("/{compraId}")
    public ResponseEntity<ApiResponse<BoletosCompraDTO>> verBoletos(
            @PathVariable Integer compraId, HttpSession session) {

        Usuario usuario = getUsuarioSesion(session);

        // Verificar que la compra pertenece al usuario antes de mapear a DTO
        Compra compra = serviceCompra.obtenerCompraPorIdConDetalles(compraId);
        if (!compra.getCliente().getId().equals(usuario.getId()))
            throw new BusinessException("No autorizado para ver esta compra");

        return ResponseEntity.ok(ApiResponse.ok("Compra obtenida",
                serviceCompra.obtenerBoletosDTO(compraId)));
    }

    // sesion logeada
    private Usuario getUsuarioSesion(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuario == null) throw new BusinessException("Usuario no autenticado");
        return usuario;
    }
}