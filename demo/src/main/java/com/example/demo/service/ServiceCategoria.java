package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.CategoriaDTO;
import com.example.demo.dto.CategoriaEventosDTO;
import com.example.demo.dto.EventoDTO;
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

    public List<Categoria> obtenerCategorias() {
        return categoriasRepository.findAll();
    }

    public List<CategoriaEventosDTO> obtenerCategoriasConEventos() {
        List<Categoria> categorias = categoriasRepository.findAll();

        return categorias.stream()
                .map(categoria -> {
                    CategoriaEventosDTO dto = new CategoriaEventosDTO();
                    dto.setId(categoria.getId());
                    dto.setNombre(categoria.getNombre());
                    List<EventoDTO> eventosDTO = serviceEvento.buscarPorCategoriaDTO(categoria.getId())
                            .stream()
                            .limit(12)
                            .collect(Collectors.toList());
                    dto.setEventos(eventosDTO);

                    long totalEventos = serviceEvento.contarEventosPorCategoria(categoria.getId());
                    dto.setTotalEventos(totalEventos);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<Categoria> obtenerTop4Categorias() {
        return categoriasRepository.findTop4ByOrderByNombreAsc();
    }

    public List<CategoriaDTO> obtenerCategoriaDTO() {
        List<Categoria> categorias = categoriasRepository.findAll();

        return categorias.stream()
                .map(categoria -> {
                    CategoriaDTO dto = new CategoriaDTO();
                    dto.setId(categoria.getId());
                    dto.setNombre(categoria.getNombre());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Categoria obtenerCategoriaPorId(Long id) {
        return categoriasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
    }

    public Categoria crearCategoria(Categoria categoria) {
        return categoriasRepository.save(categoria);
    }

    public void eliminarCategoria(Long id) {
        categoriasRepository.deleteById(id);
    }

    public boolean tieneEventosAsociados(Long id) {
        return eventoRepository.countByCategoriaId(id) > 0;
    }

    public void actualizarCategoria(Categoria categoriaExistente) {
        categoriasRepository.save(categoriaExistente);
    }

    public Categoria findByNombre(String nombre) {
        return categoriasRepository.findByNombre(nombre);
    }

}
