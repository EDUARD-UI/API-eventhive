package com.example.demo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.CompraDetalleDTO;
import com.example.demo.model.Compra;
import com.example.demo.model.Usuario;
import com.example.demo.service.ServiceCompra;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/compras")
public class CompraController {

    private final ServiceCompra serviceCompra;

    @PostMapping("/procesar")
    @ResponseBody
    public Map<String, Object> procesarCompra(
            @RequestParam Long localidadId,
            @RequestParam Integer cantidad,
            @RequestParam String metodoPago,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");
            
            if (usuario == null) {
                response.put("success", false);
                response.put("mensaje", "Debe iniciar sesión para realizar una compra");
                return response;
            }

            Compra compra = serviceCompra.procesarCompra(usuario, localidadId, cantidad, metodoPago);

            response.put("success", true);
            response.put("mensaje", "Compra realizada exitosamente");
            response.put("compraId", compra.getId());
            return response;

        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return response;
        }
    }

    @GetMapping("/historialCompras")
    public String historialCompras(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            List<Compra> compras = serviceCompra.obtenerComprasConDetallesPorCliente(usuario.getId());
            
            List<CompraDetalleDTO> comprasDetalle = compras.stream()
                .map(compra -> {
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
                            
                            if (primerTiquete.getLocalidad().getEvento() != null) {
                                var evento = primerTiquete.getLocalidad().getEvento();
                                dto.setEventoTitulo(evento.getTitulo());
                                dto.setEventoFecha(evento.getFecha());
                                dto.setEventoHora(evento.getHora());
                                dto.setEventoLugar(evento.getLugar());
                            }
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
            
            model.addAttribute("compras", comprasDetalle);
            model.addAttribute("usuarioActual", usuario);
            
            return "historialCompras";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el historial de compras");
            model.addAttribute("compras", List.of());
            return "historialCompras";
        }
    }
}