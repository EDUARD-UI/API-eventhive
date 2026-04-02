package com.example.demo.controllers;

import java.math.BigDecimal;
import java.util.List;

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
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Evento;
import com.example.demo.model.Localidad;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.GlobalController;
import com.example.demo.service.ServiceEvento;
import com.example.demo.service.ServiceLocalidad;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/localidades")
public class LocalidadesApiController {

    private final ServiceLocalidad serviceLocalidad;
    private final ServiceEvento serviceEvento;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Localidad>>> listarLocalidades() {
        return ResponseEntity.ok(ApiResponse.ok("Localidades obtenidas", serviceLocalidad.obtenerTodasLasLocalidades()));
    }

    @GetMapping("/organizador")
    public ResponseEntity<ApiResponse<List<Localidad>>> listarPorOrganizador(HttpSession session) {
        Usuario u = GlobalController.rolRequerido(usuarioRepository, "organizador");
        List<Localidad> lista = serviceLocalidad.obtenerPorOrganizador(u.getId());
        return ResponseEntity.ok(ApiResponse.ok("Localidades obtenidas", lista));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<ApiResponse<List<Localidad>>> listarPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(ApiResponse.ok("Localidades del evento",
                serviceLocalidad.obtenerLocalidadesPorEvento(eventoId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> crearLocalidad(
            @RequestParam String nombre,
            @RequestParam BigDecimal precio,
            @RequestParam Integer capacidad,
            @RequestParam Integer disponibles,
            @RequestParam Long eventoId) {

        Evento evento = serviceEvento.obtenerEventoPorId(eventoId);
        if (disponibles > capacidad) {
            throw new BusinessException("Los asientos disponibles no pueden ser mayores que la capacidad");
        }

        Localidad nuevaLocalidad = new Localidad();
        nuevaLocalidad.setNombre(nombre);
        nuevaLocalidad.setPrecio(precio);
        nuevaLocalidad.setCapacidad(capacidad);
        nuevaLocalidad.setDisponibles(disponibles);
        nuevaLocalidad.setEvento(evento);

        serviceLocalidad.crearLocalidad(nuevaLocalidad);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Localidad creada exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> actualizarLocalidad(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam BigDecimal precio,
            @RequestParam Integer capacidad,
            @RequestParam Integer disponibles,
            @RequestParam Long eventoId) {

        Localidad localidadExistente = serviceLocalidad.obtenerLocalidadPorId(id);
        if (localidadExistente == null) throw new ResourceNotFoundException("La localidad no existe");

        serviceEvento.obtenerEventoPorId(eventoId); // lanza excepción si no existe

        if (disponibles > capacidad) {
            throw new BusinessException("Los asientos disponibles no pueden ser mayores que la capacidad");
        }

        Evento evento = serviceEvento.obtenerEventoPorId(eventoId);
        localidadExistente.setNombre(nombre);
        localidadExistente.setPrecio(precio);
        localidadExistente.setCapacidad(capacidad);
        localidadExistente.setDisponibles(disponibles);
        localidadExistente.setEvento(evento);

        serviceLocalidad.actualizarLocalidad(localidadExistente);
        return ResponseEntity.ok(ApiResponse.ok("Localidad actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarLocalidad(@PathVariable Long id) {
        if (serviceLocalidad.obtenerLocalidadPorId(id) == null) {
            throw new ResourceNotFoundException("La localidad no existe");
        }
        serviceLocalidad.eliminarLocalidad(id);
        return ResponseEntity.ok(ApiResponse.ok("Localidad eliminada exitosamente"));
    }
}