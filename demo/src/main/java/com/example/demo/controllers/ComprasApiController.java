package com.example.demo.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        Usuario usuario = getUsuarioSesion(session);
        Compra compra = serviceCompra.procesarCompra(usuario, localidadId, cantidad, metodoPago);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Compra realizada exitosamente",
                        Map.of("compraId", compra.getId())));
    }

    @GetMapping("/historial")
    public ResponseEntity<ApiResponse<List<CompraDetalleDTO>>> historialCompras(HttpSession session) {
        Usuario usuario = getUsuarioSesion(session);
        List<Compra> compras = serviceCompra.obtenerComprasConDetallesPorCliente(usuario.getId());
        List<CompraDetalleDTO> dtos = compras.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok("Historial de compras", dtos));
    }

    @GetMapping("/{compraId}")
    public ResponseEntity<ApiResponse<Compra>> obtenerCompra(
            @PathVariable Integer compraId,
            HttpSession session) {

        Usuario usuario = getUsuarioSesion(session);
        Compra compra = serviceCompra.obtenerCompraPorIdConDetalles(compraId);

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

    private CompraDetalleDTO toDTO(Compra compra) {
        CompraDetalleDTO dto = new CompraDetalleDTO();
        dto.setId(compra.getId());
        dto.setFechaCompra(compra.getFechaCompra());
        dto.setTotal(compra.getTotal());
        dto.setMetodoPago(compra.getMetodoPago());

        if (compra.getTiqueteCompras() != null && !compra.getTiqueteCompras().isEmpty()) {
            var primerTiquete = compra.getTiqueteCompras().get(0).getTiquete();
            if (primerTiquete != null && primerTiquete.getLocalidad() != null) {
                dto.setLocalidadNombre(primerTiquete.getLocalidad().getNombre());
                dto.setCantidad(compra.getTiqueteCompras().size());
                var evento = primerTiquete.getLocalidad().getEvento();
                if (evento != null) {
                    dto.setEventoTitulo(evento.getTitulo());
                    dto.setEventoFecha(evento.getFecha());
                    dto.setEventoHora(evento.getHora());
                    dto.setEventoLugar(evento.getLugar());
                }
            }
        }
        return dto;
    }
}