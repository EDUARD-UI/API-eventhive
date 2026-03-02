package com.example.demo.controllers;

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
import com.example.demo.model.Estado;
import com.example.demo.service.ServiceEstado;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/estados")
public class EstadosApiController {

    private final ServiceEstado serviceEstado;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Estado>>> listarEstados() {
        return ResponseEntity.ok(ApiResponse.ok("Estados obtenidos", serviceEstado.obtenerEstados()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Estado>> obtenerEstado(@PathVariable Long id) {
        Estado estado = serviceEstado.findById(id);
        if (estado == null) throw new ResourceNotFoundException("Estado no encontrado");
        return ResponseEntity.ok(ApiResponse.ok("Estado obtenido", estado));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> crearEstado(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion) {

        if (serviceEstado.findByNombre(nombre) != null) {
            throw new BusinessException("Ya existe un estado con ese nombre");
        }

        Estado nuevoEstado = new Estado();
        nuevoEstado.setNombre(nombre);
        nuevoEstado.setDescripcion(descripcion);
        serviceEstado.crearEstado(nuevoEstado);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Estado creado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion) {

        Estado estadoExistente = serviceEstado.findById(id);
        if (estadoExistente == null) throw new ResourceNotFoundException("El estado no existe");

        Estado estadoConNombre = serviceEstado.findByNombre(nombre);
        if (estadoConNombre != null && estadoConNombre.getId() != id) {
            throw new BusinessException("Ya existe otro estado con ese nombre");
        }

        estadoExistente.setNombre(nombre);
        estadoExistente.setDescripcion(descripcion);
        serviceEstado.actualizarEstado(estadoExistente);

        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarEstado(@PathVariable Long id) {
        if (serviceEstado.findById(id) == null) throw new ResourceNotFoundException("El estado no existe");

        if (serviceEstado.tieneEntidadesAsociadas(id)) {
            throw new BusinessException("No se puede eliminar el estado porque está siendo utilizado");
        }

        serviceEstado.eliminarEstado(id);
        return ResponseEntity.ok(ApiResponse.ok("Estado eliminado exitosamente"));
    }
}