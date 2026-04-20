package com.example.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.CategoriaDTO;
import com.example.demo.dto.CategoriaEventosDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Categoria;
import com.example.demo.model.Evento;
import com.example.demo.repository.CategoriasRepository;
import com.example.demo.repository.EventoRepository;
import com.example.demo.utils.Utilidades;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceCategoria {

    private final CategoriasRepository categoriasRepository;
    private final EventoRepository eventoRepository;

    @Value("${upload.path:uploads/categorias}")
    private String uploadPath;

    public Page<Categoria> obtenerTodasCategorias(Pageable pageable) {
        return categoriasRepository.findAll(pageable);
    }

    public List<Categoria> obtenerTodas() {
        return categoriasRepository.findAll();
    }

    public Categoria obtenerCategoriaPorId(String id) {
        return categoriasRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Categoría no encontrada"));
    }

    public List<CategoriaDTO> obtenerCategoriaDTO() {
        return categoriasRepository.findAll().stream()
            .map(c -> new CategoriaDTO(c.getId(), c.getNombre()))
            .collect(Collectors.toList());
    }

    public List<Categoria> obtenerTop4Categorias() {
        return categoriasRepository.findTop4ByOrderByNombreAsc();
    }

    public List<CategoriaEventosDTO> obtenerCategoriasConEventos() {
        return categoriasRepository.findAll().stream()
            .map(categoria -> {
                List<Evento> eventos = eventoRepository.findByCategoriaId(categoria.getId());
                CategoriaEventosDTO dto = new CategoriaEventosDTO();
                dto.setId(categoria.getId());
                dto.setNombre(categoria.getNombre());
                dto.setTotalEventos(eventos.size());
                return dto;
            })
            .collect(Collectors.toList());
    }

    public void crearCategoria(String nombre, MultipartFile foto) throws IOException {
        if (categoriasRepository.findByNombre(nombre) != null) {
            throw new BusinessException("Categoría ya existe");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);

        if (foto != null && !foto.isEmpty()) {
            Utilidades.validarFoto(foto);
            String nombreFoto = Utilidades.guardarFoto(foto, uploadPath);
            categoria.setFoto(nombreFoto);
        }

        categoriasRepository.save(categoria);
    }

    public void actualizarCategoria(String id, String nombre, MultipartFile foto) throws IOException {
        Categoria existente = obtenerCategoriaPorId(id);

        Categoria conNombre = categoriasRepository.findByNombre(nombre);
        if (conNombre != null && !conNombre.getId().equals(id)) {
            throw new BusinessException("Nombre ya existe");
        }

        existente.setNombre(nombre);

        if (foto != null && !foto.isEmpty()) {
            Utilidades.validarFoto(foto);
            if (existente.getFoto() != null) {
                Utilidades.eliminarFoto(existente.getFoto(), uploadPath);
            }
            String nombreFoto = Utilidades.guardarFoto(foto, uploadPath);
            existente.setFoto(nombreFoto);
        }

        categoriasRepository.save(existente);
    }

    public void eliminarCategoria(String id) {
        if (contarEventosPorCategoria(id) > 0) {
            throw new BusinessException("No se puede eliminar categoría con eventos");
        }

        Categoria categoria = obtenerCategoriaPorId(id);
        if (categoria.getFoto() != null) {
            Utilidades.eliminarFoto(categoria.getFoto(), uploadPath);
        }

        categoriasRepository.deleteById(id);
    }

    public long contarEventosPorCategoria(String categoriaId) {
        return eventoRepository.countByCategoriaId(categoriaId);
    }
}