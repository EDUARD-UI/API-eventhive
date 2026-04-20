package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.Categoria;
import com.example.demo.repository.CategoriasRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceCategoria {

    private final CategoriasRepository categoriasRepository;
    private final ServiceEvento serviceEvento;

    public List<Categoria> obtenerTodasCategorias() {
        return categoriasRepository.findAll();
    }

    public Categoria obtenerCategoriaPorId(String id) {
        return categoriasRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoría no encontrada"));
    }

    public Categoria findByNombre(String nombre) {
        return categoriasRepository.findByNombre(nombre);
    }

    public boolean tieneEventosAsociados(String id) {
        return serviceEvento.contarEventosPorCategoria(id) > 0;
    }

    public void crearCategoria(Categoria categoria) {
        if (categoriasRepository.findByNombre(categoria.getNombre()) != null) {
            throw new BusinessException("Ya existe una categoría con ese nombre");
        }
        categoriasRepository.save(categoria);
    }

    public void actualizarCategoria(String id, Categoria categoriaActualizada) {
        Categoria existente = obtenerCategoriaPorId(id);
        
        Categoria conNombre = categoriasRepository.findByNombre(categoriaActualizada.getNombre());
        if (conNombre != null && !conNombre.getId().equals(id)) {
            throw new BusinessException("Ya existe otra categoría con ese nombre");
        }
        
        existente.setNombre(categoriaActualizada.getNombre());
        existente.setFoto(categoriaActualizada.getFoto());
        categoriasRepository.save(existente);
    }

    public void eliminarCategoria(String id) {
        if (tieneEventosAsociados(id)) {
            throw new BusinessException("No se puede eliminar la categoría porque tiene eventos asociados");
        }
        categoriasRepository.deleteById(id);
    }
}