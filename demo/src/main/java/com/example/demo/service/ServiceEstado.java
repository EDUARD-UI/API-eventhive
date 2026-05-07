package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Estado;
import com.example.demo.repository.EstadoRepository;
import com.example.demo.repository.EventoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceEstado {

    private final EstadoRepository estadoRepository;
    private final EventoRepository eventoRepository;
    private final MongoTemplate    mongoTemplate;

    public Estado findByNombre(String nombre) {
        return estadoRepository.findByNombre(nombre);
    }

    public List<Estado> obtenerEstados() {
        return estadoRepository.findAll();
    }

    public Page<Estado> obtenerEstados(Pageable pageable) {
        return estadoRepository.findAll(pageable);
    }

    public Estado obtenerEstadoPorId(String id) {
        return estadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado"));
    }

    public void crearEstado(String nombre, String descripcion) {
        if (estadoRepository.existsByNombre(nombre))
            throw new BusinessException("Ya existe un estado con ese nombre");

        Estado nuevo = new Estado();
        nuevo.setNombre(nombre);
        nuevo.setDescripcion(descripcion);
        estadoRepository.save(nuevo);
    }

    public void actualizarEstado(String id, String nombre, String descripcion) {
        Estado existente = obtenerEstadoPorId(id);

        // Solo validar duplicado si el nombre realmente cambia
        if (!existente.getNombre().equalsIgnoreCase(nombre)) {
            if (estadoRepository.existsByNombre(nombre))
                throw new BusinessException("Ya existe otro estado con ese nombre");
        }

        //coreccion aun en prueba
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
                .set("nombre", nombre)
                .set("descripcion", descripcion);
        mongoTemplate.updateFirst(query, update, Estado.class);
    }

    public void eliminarEstado(String id) {
        Estado estado = obtenerEstadoPorId(id);

        long eventosConEstado = eventoRepository.countByEstadoId(id);
        if (eventosConEstado > 0)
            throw new BusinessException(
                "No se puede eliminar el estado '" + estado.getNombre() + "' porque tiene "
                + eventosConEstado + " evento(s) asociado(s)");

        estadoRepository.deleteById(id);
    }
}
