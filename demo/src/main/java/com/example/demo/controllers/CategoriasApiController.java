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
import com.example.demo.dto.PagedResponse;
import com.example.demo.model.Categoria;
import com.example.demo.service.ServiceCategoria;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categorias")
public class CategoriasApiController {

    private final ServiceCategoria serviceCategoria;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Categoria>>> obtener(Pageable pageable) {
        Page<Categoria> page = serviceCategoria.obtenerTodasCategorias(pageable);

        PagedResponse<Categoria> response = new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ResponseEntity.ok(ApiResponse.ok("Categorías obtenidas", response));
    }

    @GetMapping("/nombres")
    public ResponseEntity<ApiResponse<List<CategoriaDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Categorías obtenidas",
                serviceCategoria.obtenerCategoriaDTO()));
    }

    @GetMapping("/destacadas")
    public ResponseEntity<ApiResponse<List<Categoria>>> destacadas() {
        return ResponseEntity.ok(ApiResponse.ok("Categorías destacadas",
                serviceCategoria.obtenerTop4Categorias()));
    }

    @GetMapping("/con-eventos")
    public ResponseEntity<ApiResponse<List<CategoriaEventosDTO>>> conEventos() {
        return ResponseEntity.ok(ApiResponse.ok("Categorías con eventos",
                serviceCategoria.obtenerCategoriasConEventos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Categoria>> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Categoría obtenida",
                serviceCategoria.obtenerCategoriaPorId(id)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> crear(
            @RequestParam String nombre,
            @RequestParam(required = false) MultipartFile foto) throws IOException {
        serviceCategoria.crearCategoria(nombre, foto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Categoría creada"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> actualizar(
            @PathVariable String id,
            @RequestParam String nombre,
            @RequestParam(required = false) MultipartFile foto) throws IOException {
        serviceCategoria.actualizarCategoria(id, nombre, foto);
        return ResponseEntity.ok(ApiResponse.ok("Categoría actualizada"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String id) {
        serviceCategoria.eliminarCategoria(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoría eliminada"));
    }
}
