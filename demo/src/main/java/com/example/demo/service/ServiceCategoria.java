package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.CategoriaDTO;
import com.example.demo.dto.CategoriaEventosDTO;
import com.example.demo.dto.EventoDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Categoria;
import com.example.demo.repository.CategoriasRepository;
import com.example.demo.repository.EventoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceCategoria {

    private final CategoriasRepository categoriasRepository;
    private final ServiceEvento serviceEvento;
    private final EventoRepository eventoRepository;

    private static final String UPLOAD_PATH = "uploads/categorias";

    public List<Categoria> obtenerTodasCategorias()        { return categoriasRepository.findAll(); }
    public List<Categoria> obtenerTop4Categorias()         { return categoriasRepository.findTop4ByOrderByNombreAsc(); }
    public Categoria findByNombre(String nombre)           { return categoriasRepository.findByNombre(nombre); }
    public boolean tieneEventosAsociados(Long id)          { return eventoRepository.countByCategoriaId(id) > 0; }

    public Categoria obtenerCategoriaPorId(Long id) {
        return categoriasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    public List<CategoriaDTO> obtenerCategoriaDTO() {
        return categoriasRepository.findAll().stream()
                .map(c -> { CategoriaDTO dto = new CategoriaDTO(); dto.setId(c.getId()); dto.setNombre(c.getNombre()); return dto; })
                .collect(Collectors.toList());
    }

    public List<CategoriaEventosDTO> obtenerCategoriasConEventos() {
        return categoriasRepository.findAll().stream()
                .map(c -> {
                    CategoriaEventosDTO dto = new CategoriaEventosDTO();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    List<EventoDTO> eventos = serviceEvento.buscarPorCategoriaDTO(c.getId())
                            .stream().limit(8).collect(Collectors.toList());
                    dto.setEventos(eventos);
                    dto.setTotalEventos(serviceEvento.contarEventosPorCategoria(c.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // crea la categoría; valida duplicado y guarda foto si viene
    public void crearCategoria(String nombre, MultipartFile foto) throws IOException {
        if (categoriasRepository.findByNombre(nombre) != null)
            throw new BusinessException("Ya existe una categoría con ese nombre");

        Categoria nueva = new Categoria();
        nueva.setNombre(nombre);
        if (foto != null && !foto.isEmpty()) {
            validarFoto(foto);
            nueva.setFoto(guardarFoto(foto));
        }
        categoriasRepository.save(nueva);
    }

    // actualiza la categoría; valida duplicado y reemplaza foto si viene
    public void actualizarCategoria(Long id, String nombre, MultipartFile foto) throws IOException {
        Categoria existente = obtenerCategoriaPorId(id);

        Categoria conNombre = categoriasRepository.findByNombre(nombre);
        if (conNombre != null && !conNombre.getId().equals(id))
            throw new BusinessException("Ya existe otra categoría con ese nombre");

        existente.setNombre(nombre);
        if (foto != null && !foto.isEmpty()) {
            validarFoto(foto);
            eliminarFoto(existente.getFoto());
            existente.setFoto(guardarFoto(foto));
        }
        categoriasRepository.save(existente);
    }

    // elimina si no tiene eventos asociados
    public void eliminarCategoria(Long id) {
        Categoria categoria = obtenerCategoriaPorId(id);
        if (tieneEventosAsociados(id))
            throw new BusinessException("No se puede eliminar la categoría porque tiene eventos asociados");
        eliminarFoto(categoria.getFoto());
        categoriasRepository.deleteById(id);
    }

    //metodos de apoyo
    private void validarFoto(MultipartFile foto) {
        if (foto.getSize() > 5 * 1024 * 1024)
            throw new BusinessException("La foto no puede superar los 5MB");
        String ct = foto.getContentType();
        if (ct == null || !ct.startsWith("image/"))
            throw new BusinessException("Solo se permiten archivos de imagen");
    }

    private String guardarFoto(MultipartFile foto) throws IOException {
        Path dir = Paths.get(UPLOAD_PATH);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        String ext = "";
        String original = foto.getOriginalFilename();
        if (original != null && original.contains("."))
            ext = original.substring(original.lastIndexOf("."));
        String fileName = UUID.randomUUID() + ext;
        Files.copy(foto.getInputStream(), dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private void eliminarFoto(String nombreFoto) {
        if (nombreFoto != null && !nombreFoto.isBlank()) {
            try { Files.deleteIfExists(Paths.get(UPLOAD_PATH).resolve(nombreFoto)); }
            catch (IOException e) { System.err.println("Error al eliminar foto: " + e.getMessage()); }
        }
    }
}