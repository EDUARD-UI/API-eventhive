package com.example.demo.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CategoriaDTO;
import com.example.demo.dto.CategoriaEventosDTO;
import com.example.demo.model.Categoria;
import com.example.demo.service.ServiceCategoria;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categorias")
public class CategoriasApiController {

    private final ServiceCategoria serviceCategoria;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Categoria>>> obtenerNombresCategorias(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Categorías obtenidas", 
            serviceCategoria.obtenerTodasCategorias(pageable)));
    }

    @GetMapping("/nombres")
    public ResponseEntity<ApiResponse<List<CategoriaDTO>>> listarCategorias() {
        return ResponseEntity.ok(ApiResponse.ok("Nombres de categorías obtenidos", 
            serviceCategoria.obtenerCategoriaDTO()));
    }

    @GetMapping("/destacadas")
    public ResponseEntity<ApiResponse<List<Categoria>>> obtenerCategoriasDestacadas() {
        return ResponseEntity.ok(ApiResponse.ok("Categorías destacadas", 
            serviceCategoria.obtenerTop4Categorias()));
    }

    @GetMapping("/con-eventos")
    public ResponseEntity<ApiResponse<List<CategoriaEventosDTO>>> listarCategoriasConEventos() {
        return ResponseEntity.ok(ApiResponse.ok("Categorías con eventos", 
            serviceCategoria.obtenerCategoriasConEventos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Categoria>> obtenerCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Categoría obtenida", 
            serviceCategoria.obtenerCategoriaPorId(id)));
    }

    //CRUD
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> crearCategoria(
            @RequestParam String nombre,
            @RequestParam(required = false) MultipartFile foto) throws IOException {
        serviceCategoria.crearCategoria(nombre, foto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Categoría creada exitosamente"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> actualizarCategoria(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam(required = false) MultipartFile foto) throws IOException {
        serviceCategoria.actualizarCategoria(id, nombre, foto);
        return ResponseEntity.ok(ApiResponse.ok("Categoría actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminarCategoria(@PathVariable Long id) {
        serviceCategoria.eliminarCategoria(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoría eliminada exitosamente"));
    }
}
