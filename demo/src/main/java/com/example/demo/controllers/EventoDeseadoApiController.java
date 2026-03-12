package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Evento;
import com.example.demo.model.EventoDeseado;
import com.example.demo.model.Usuario;
import com.example.demo.service.ServiceEvento;
import com.example.demo.service.ServiceEventoDeseado;
import com.example.demo.dto.EventoDeseadoDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eventos-deseados")
public class EventoDeseadoApiController {

    private final ServiceEventoDeseado serviceEventoDeseado;
    private final ServiceEvento serviceEvento;

    //eventos deseados del usuario en sesión
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventoDeseadoDTO>>> listarDeseadosDelUsuario(HttpSession session) {
        List<EventoDeseadoDTO> dtos = serviceEventoDeseado
                .obtenerDeseadosDTOPorUsuario(getUsuarioSesion(session).getId());
        return ResponseEntity.ok(ApiResponse.ok("Eventos deseados obtenidos", dtos));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> agregarDeseado(
            @RequestParam Long eventoId,
            HttpSession session) {

        Usuario usuario = getUsuarioSesion(session);
        Evento evento = serviceEvento.obtenerEventoPorId(eventoId);

        EventoDeseado ed = new EventoDeseado();
        ed.setUsuario(usuario);
        ed.setEvento(evento);
        serviceEventoDeseado.guardarEventoDeseado(ed);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Evento agregado a deseados"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDeseado(
            @PathVariable long id,
            HttpSession session) {

        Usuario usuario = getUsuarioSesion(session);
        EventoDeseado ed = serviceEventoDeseado.obtenerEventoDeseadoPorId(id);

        if (!ed.getUsuario().getId().equals(usuario.getId()))
            throw new BusinessException("No autorizado para eliminar este evento deseado");

        serviceEventoDeseado.eliminarEventoDeseado(id);
        return ResponseEntity.ok(ApiResponse.ok("Evento eliminado de deseados"));
    }

    //validar usuario logeado
    private Usuario getUsuarioSesion(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuario == null) throw new BusinessException("Debe iniciar sesión para realizar esta acción");
        return usuario;
    }
}
