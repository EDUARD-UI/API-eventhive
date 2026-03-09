package com.example.demo.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Categoria;
import com.example.demo.service.ServiceCategoria;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categorias")
public class CategoriasApiController {

    private final ServiceCategoria serviceCategoria;
    private final String uploadPath = "uploads/categorias";

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaDTO>>> listarCategorias() {
        List<CategoriaDTO> categorias = serviceCategoria.obtenerCategoriaDTO();
        return ResponseEntity.ok(ApiResponse.ok("Categorías obtenidas exitosamente", categorias));
    }

    @GetMapping("/destacadas")
    public ResponseEntity<ApiResponse<List<Categoria>>> obtenerCategoriasDestacadas() {
        List<Categoria> categorias = serviceCategoria.obtenerTop4Categorias();
        return ResponseEntity.ok(ApiResponse.ok("Categorías destacadas obtenidas exitosamente", categorias));
    }
    

    @GetMapping("/con-eventos")
    public ResponseEntity<ApiResponse<List<CategoriaEventosDTO>>> listarCategoriasConEventos() {
        List<CategoriaEventosDTO> categorias = serviceCategoria.obtenerCategoriasConEventos();
        return ResponseEntity.ok(ApiResponse.ok("Categorías con eventos obtenidas exitosamente", categorias));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Categoria>> obtenerCategoria(@PathVariable Long id) {
        Categoria categoria = serviceCategoria.obtenerCategoriaPorId(id);
        if (categoria == null) throw new ResourceNotFoundException("Categoría no encontrada");
        return ResponseEntity.ok(ApiResponse.ok("Categoría obtenida", categoria));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> crearCategoria(
            @RequestParam String nombre,
            @RequestParam(required = false) MultipartFile foto) throws IOException {

        if (serviceCategoria.findByNombre(nombre) != null) {
            throw new BusinessException("Ya existe una categoría con ese nombre");
        }

        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombre(nombre);

        if (foto != null && !foto.isEmpty()) {
            validarFoto(foto);
            nuevaCategoria.setFoto(guardarFoto(foto));
        }

        serviceCategoria.crearCategoria(nuevaCategoria);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Categoría creada exitosamente"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> actualizarCategoria(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam(required = false) MultipartFile foto) throws IOException {

        Categoria categoriaExistente = serviceCategoria.obtenerCategoriaPorId(id);
        if (categoriaExistente == null) throw new ResourceNotFoundException("La categoría no existe");

        Categoria categoriaConNombre = serviceCategoria.findByNombre(nombre);
        if (categoriaConNombre != null && !categoriaConNombre.getId().equals(id)) {
            throw new BusinessException("Ya existe otra categoría con ese nombre");
        }

        categoriaExistente.setNombre(nombre);

        if (foto != null && !foto.isEmpty()) {
            validarFoto(foto);
            eliminarFoto(categoriaExistente.getFoto());
            categoriaExistente.setFoto(guardarFoto(foto));
        }

        serviceCategoria.actualizarCategoria(categoriaExistente);
        return ResponseEntity.ok(ApiResponse.ok("Categoría actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCategoria(@PathVariable Long id) {
        Categoria categoria = serviceCategoria.obtenerCategoriaPorId(id);
        if (categoria == null) throw new ResourceNotFoundException("La categoría no existe");

        if (serviceCategoria.tieneEventosAsociados(id)) {
            throw new BusinessException("No se puede eliminar la categoría porque tiene eventos asociados");
        }

        eliminarFoto(categoria.getFoto());
        serviceCategoria.eliminarCategoria(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoría eliminada exitosamente"));
    }

    // FUNCIONES DE APOYO
    private void validarFoto(MultipartFile foto) {
        if (foto.getSize() > 5 * 1024 * 1024) throw new BusinessException("La foto no puede superar los 5MB");
        String ct = foto.getContentType();
        if (ct == null || !ct.startsWith("image/")) throw new BusinessException("Solo se permiten archivos de imagen");
    }

    private String guardarFoto(MultipartFile foto) throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
        String ext = "";
        String original = foto.getOriginalFilename();
        if (original != null && original.contains(".")) ext = original.substring(original.lastIndexOf("."));
        String fileName = UUID.randomUUID() + ext;
        Files.copy(foto.getInputStream(), uploadDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private void eliminarFoto(String nombreFoto) {
        if (nombreFoto != null && !nombreFoto.isBlank()) {
            try { Files.deleteIfExists(Paths.get(uploadPath).resolve(nombreFoto)); }
            catch (IOException e) { System.err.println("Error al eliminar foto: " + e.getMessage()); }
        }
    }
}