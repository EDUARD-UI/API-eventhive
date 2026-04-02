package com.example.demo.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Evento;
import com.example.demo.model.Promocion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.GlobalController;
import com.example.demo.service.ServiceEvento;
import com.example.demo.service.ServicePromocion;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promociones")
public class PromocionApiController {

    private final ServicePromocion servicePromocion;
    private final ServiceEvento serviceEvento;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/organizador")
    public ResponseEntity<ApiResponse<List<PromocionDTO>>> listarPorOrganizador(HttpSession session) {
        Usuario u = GlobalController.rolRequerido(usuarioRepository, "organizador");
        List<PromocionDTO> lista = servicePromocion.obtenerPorOrganizador(u.getId())
            .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok("Promociones obtenidas", lista));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> crear(
            @RequestParam Long eventoId,
            @RequestParam String descripcion,
            @RequestParam BigDecimal descuento,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            HttpSession session) {

        Usuario u = GlobalController.rolRequerido(usuarioRepository, "organizador");
        validarDescuento(descuento);

        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin    = LocalDate.parse(fechaFin);
        if (fin.isBefore(inicio))
            throw new BusinessException("La fecha de fin no puede ser anterior a la de inicio");

        Evento evento = serviceEvento.obtenerEventoPorId(eventoId);
        if (!evento.getUsuario().getId().equals(u.getId()))
            throw new BusinessException("No puede crear promociones para eventos de otro organizador");

        Promocion p = new Promocion();
        p.setEvento(evento);
        p.setDescripcion(descripcion);
        p.setDescuento(descuento);
        p.setFechaInicio(inicio);
        p.setFechaFinal(fin);
        servicePromocion.crearPromocion(p);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Promoción creada exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> actualizar(
            @PathVariable Long id,
            @RequestParam Long eventoId,
            @RequestParam String descripcion,
            @RequestParam BigDecimal descuento,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            HttpSession session) {

        Usuario u = GlobalController.rolRequerido(usuarioRepository, "organizador");
        Promocion p = servicePromocion.obtenerPromocionPorId(id);
        if (p == null) throw new BusinessException("La promoción no existe");
        if (!p.getEvento().getUsuario().getId().equals(u.getId()))
            throw new BusinessException("No autorizado");

        validarDescuento(descuento);
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin    = LocalDate.parse(fechaFin);
        if (fin.isBefore(inicio))
            throw new BusinessException("La fecha de fin no puede ser anterior a la de inicio");

        Evento evento = serviceEvento.obtenerEventoPorId(eventoId);
        p.setEvento(evento);
        p.setDescripcion(descripcion);
        p.setDescuento(descuento);
        p.setFechaInicio(inicio);
        p.setFechaFinal(fin);
        servicePromocion.actualizarPromocion(p);

        return ResponseEntity.ok(ApiResponse.ok("Promoción actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long id, HttpSession session) {

        Usuario u = GlobalController.rolRequerido(usuarioRepository, "organizador");
        Promocion p = servicePromocion.obtenerPromocionPorId(id);
        if (p == null) throw new BusinessException("La promoción no existe");
        if (!p.getEvento().getUsuario().getId().equals(u.getId()))
            throw new BusinessException("No autorizado");

        servicePromocion.eliminarPromocion(id);
        return ResponseEntity.ok(ApiResponse.ok("Promoción eliminada exitosamente"));
    }

    // funciones de apoyo
    private void validarDescuento(BigDecimal d) {
        if (d.compareTo(BigDecimal.ONE) < 0 || d.compareTo(new BigDecimal("75")) > 0)
            throw new BusinessException("El descuento debe estar entre 1 y 75");
    }

    private PromocionDTO toDTO(Promocion p) {
        PromocionDTO dto = new PromocionDTO();
        dto.setId(p.getId());
        dto.setDescripcion(p.getDescripcion());
        dto.setDescuento(p.getDescuento());
        dto.setFechaInicio(p.getFechaInicio());
        dto.setFechaFinal(p.getFechaFinal());
        if (p.getEvento() != null) {
            dto.setEventoId(p.getEvento().getId());
            dto.setEventoTitulo(p.getEvento().getTitulo());
        }
        return dto;
    }
}
