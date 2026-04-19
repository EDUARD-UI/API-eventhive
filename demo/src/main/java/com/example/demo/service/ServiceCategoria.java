package com.example.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.example.demo.utils.Utilidades;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceCategoria {

    private final CategoriasRepository categoriasRepository;
    private final ServiceEvento serviceEvento;
    private final EventoRepository eventoRepository;

    private static final String UPLOAD_PATH = "uploads/categorias";

    public List<Categoria> obtenerTodasCategorias() {
        return categoriasRepository.findAll();
    }

    public List<Categoria> obtenerTop4Categorias() {
        return categoriasRepository.findTop4ByOrderByNombreAsc();
    }

    public Categoria findByNombre(String nombre) {
        return categoriasRepository.findByNombre(nombre);
    }

    public boolean tieneEventosAsociados(String id) {
        return eventoRepository.countByCategoriaId(id) > 0;
    }

    public Page<Categoria> obtenerTodasCategorias(Pageable pageable) {
        return categoriasRepository.findAll(pageable);
    }

    public Categoria obtenerCategoriaPorId(String id) {
        return categoriasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    public List<CategoriaDTO> obtenerCategoriaDTO() {
        return categoriasRepository.findAll().stream()
                .map(c -> {
                    CategoriaDTO dto = new CategoriaDTO();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<CategoriaEventosDTO> obtenerCategoriasConEventos() {
        return categoriasRepository.findAll().stream()
                .map(this::mapearCategoriaConEventos)
                .collect(Collectors.toList());
    }

    public void crearCategoria(String nombre, MultipartFile foto) throws IOException {
        if (categoriasRepository.findByNombre(nombre) != null)
            throw new BusinessException("Ya existe una categoría con ese nombre");

        Categoria nueva = new Categoria();
        nueva.setNombre(nombre);
        if (foto != null && !foto.isEmpty()) {
            Utilidades.validarFoto(foto);
            nueva.setFoto(Utilidades.guardarFoto(foto, UPLOAD_PATH));
        }
        categoriasRepository.save(nueva);
    }

    public void actualizarCategoria(String id, String nombre, MultipartFile foto) throws IOException {
        Categoria existente = obtenerCategoriaPorId(id);

        Categoria conNombre = categoriasRepository.findByNombre(nombre);
        if (conNombre != null && !conNombre.getId().equals(id))
            throw new BusinessException("Ya existe otra categoría con ese nombre");

        existente.setNombre(nombre);
        if (foto != null && !foto.isEmpty()) {
            Utilidades.validarFoto(foto);
            Utilidades.eliminarFoto(existente.getFoto(), UPLOAD_PATH);
            existente.setFoto(Utilidades.guardarFoto(foto, UPLOAD_PATH));
        }
        categoriasRepository.save(existente);
    }

    public void eliminarCategoria(String id) {
        Categoria categoria = obtenerCategoriaPorId(id);
        if (tieneEventosAsociados(id))
            throw new BusinessException("No se puede eliminar la categoría porque tiene eventos asociados");
        Utilidades.eliminarFoto(categoria.getFoto(), UPLOAD_PATH);
        categoriasRepository.deleteById(id);
    }

    private CategoriaEventosDTO mapearCategoriaConEventos(Categoria c) {
        CategoriaEventosDTO dto = new CategoriaEventosDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        
        List<EventoDTO> eventos = serviceEvento.buscarPorCategoriaDTO(c.getId())
                .stream()
                .limit(8)
                .collect(Collectors.toList());
        
        dto.setEventos(eventos);
        dto.setTotalEventos((int) serviceEvento.contarEventosPorCategoria(c.getId()));
        
        return dto;
    }
}
