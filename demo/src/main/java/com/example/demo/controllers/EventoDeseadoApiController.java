package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.EventoDeseadoDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Evento;
import com.example.demo.model.EventoDeseado;
import com.example.demo.model.Usuario;
import com.example.demo.security.SecurityController;
import com.example.demo.service.ServiceEvento;
import com.example.demo.service.ServiceEventoDeseado;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eventos-deseados")
public class EventoDeseadoApiController {

    private final ServiceEventoDeseado serviceEventoDeseado;
    private final ServiceEvento serviceEvento;
    private final SecurityController securityController;

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<List<EventoDeseadoDTO>>> listarDeseadosDelUsuario() {
        return ResponseEntity.ok(ApiResponse.ok("Eventos deseados obtenidos",
                serviceEventoDeseado.obtenerDeseadosDTOPorUsuario(usuarioAutenticado().getId())));
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<Void>> agregarDeseado(@RequestParam Long eventoId) {
        Usuario usuario = usuarioAutenticado();
        Evento evento = serviceEvento.obtenerEventoPorId(eventoId);

        EventoDeseado ed = new EventoDeseado();
        ed.setUsuario(usuario);
        ed.setEvento(evento);
        serviceEventoDeseado.guardarEventoDeseado(ed);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Evento agregado a deseados"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<Void>> eliminarDeseado(@PathVariable long id) {
        Usuario usuario = usuarioAutenticado();
        EventoDeseado ed = serviceEventoDeseado.obtenerEventoDeseadoPorId(id);

        if (!ed.getUsuario().getId().equals(usuario.getId()))
            throw new BusinessException("No autorizado para eliminar este evento deseado");

        serviceEventoDeseado.eliminarEventoDeseado(id);
        return ResponseEntity.ok(ApiResponse.ok("Evento eliminado de deseados"));
    }

    private Usuario usuarioAutenticado() {
        return securityController.usuarioAutenticado();
    }
}