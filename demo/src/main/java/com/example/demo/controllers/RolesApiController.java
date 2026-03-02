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
import com.example.demo.model.Rol;
import com.example.demo.service.ServiceEstado;
import com.example.demo.service.ServiceRoles;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RolesApiController {

    private final ServiceRoles serviceRoles;
    private final ServiceEstado serviceEstado;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Rol>>> listarRoles() {
        return ResponseEntity.ok(ApiResponse.ok("Roles obtenidos", serviceRoles.obtenerTodosRoles()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> crearRol(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam Long estadoId) {

        if (serviceRoles.findByNombre(nombre) != null) {
            throw new BusinessException("Ya existe un rol con ese nombre");
        }

        Estado estado = serviceEstado.findById(estadoId);
        if (estado == null) throw new ResourceNotFoundException("El estado especificado no existe");

        Rol nuevoRol = new Rol();
        nuevoRol.setNombre(nombre);
        nuevoRol.setDescripcion(descripcion);
        nuevoRol.setEstado(estado);
        serviceRoles.crearRol(nuevoRol);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Rol creado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> actualizarRol(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam Long estadoId) {

        Rol rolExistente = serviceRoles.findById(id);
        if (rolExistente == null) throw new ResourceNotFoundException("El rol no existe");

        Rol rolConNombre = serviceRoles.findByNombre(nombre);
        if (rolConNombre != null && !rolConNombre.getId().equals(id)) {
            throw new BusinessException("Ya existe otro rol con ese nombre");
        }

        Estado estado = serviceEstado.findById(estadoId);
        if (estado == null) throw new ResourceNotFoundException("El estado especificado no existe");

        rolExistente.setNombre(nombre);
        rolExistente.setDescripcion(descripcion);
        rolExistente.setEstado(estado);
        serviceRoles.actualizarRol(rolExistente);

        return ResponseEntity.ok(ApiResponse.ok("Rol actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarRol(@PathVariable Long id) {
        if (serviceRoles.findById(id) == null) throw new ResourceNotFoundException("El rol no existe");

        if (serviceRoles.tieneUsuariosAsociados(id)) {
            throw new BusinessException("No se puede eliminar el rol porque tiene usuarios asociados");
        }

        serviceRoles.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Rol eliminado exitosamente"));
    }
}