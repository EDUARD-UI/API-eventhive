package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.EventoDeseado;
import com.example.demo.service.ServiceEventoDeseado;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/eventos-deseados")
public class EventoDeseadoApiController {

    private final ServiceEventoDeseado serviceEventoDeseado;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventoDeseado>>> listarEventoDeseado() {
        return ResponseEntity.ok(ApiResponse.ok("eventos deseados obtenidos"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventoDeseado>> obtenerEventoDeseado(@PathVariable long id) {
        EventoDeseado eventoDeseado = serviceEventoDeseado.obtenerEventoDeseadoPorId(id);
        if (eventoDeseado == null) throw new ResourceNotFoundException("evento deseado no encontrado");
        return ResponseEntity.ok(ApiResponse.ok("Evento deseado no obtenido", eventoDeseado));
    }
    
}
