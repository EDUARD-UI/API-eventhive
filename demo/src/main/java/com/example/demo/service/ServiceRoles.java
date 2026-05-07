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
import com.example.demo.model.Rol;
import com.example.demo.repository.RolesRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceRoles {

    private final RolesRepository  rolesRepository;
    private final UsuarioRepository usuarioRepository;
    private final MongoTemplate     mongoTemplate;

    public Rol findById(String id) {
        return rolesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
    }

    public Rol findByNombre(String nombre) {
        return rolesRepository.findByNombre(nombre);
    }

    public List<Rol> obtenerTodosRoles() {
        return rolesRepository.findAll();
    }

    public Page<Rol> obtenerTodosRoles(Pageable pageable) {
        return rolesRepository.findAll(pageable);
    }

    public boolean tieneUsuariosAsociados(String rolId) {
        return usuarioRepository.countByRolId(rolId) > 0;
    }

    public void crearRol(String nombre, String descripcion) {
        if (rolesRepository.existsByNombre(nombre))
            throw new BusinessException("Ya existe un rol con ese nombre");

        Rol nuevo = new Rol();
        nuevo.setNombre(nombre);
        nuevo.setDescripcion(descripcion);
        rolesRepository.save(nuevo);
    }

    public void actualizarRol(String id, String nombre, String descripcion) {
        // 1. Verificar que el rol existe
        Rol existente = rolesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));

        if (!existente.getNombre().equalsIgnoreCase(nombre)) {
            if (rolesRepository.existsByNombre(nombre))
                throw new BusinessException("Ya existe otro rol con ese nombre");
        }

        //coreccion en prueba
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
                .set("nombre", nombre)
                .set("descripcion", descripcion);
        mongoTemplate.updateFirst(query, update, Rol.class);
    }

    public void eliminarRol(String id) {
        // 1. Verificar que el rol existe
        Rol rol = rolesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));

        // 2. Verificar que no tenga usuarios asociados
        long usuariosConRol = usuarioRepository.countByRolId(id);
        if (usuariosConRol > 0)
            throw new BusinessException(
                "No se puede eliminar el rol '" + rol.getNombre() + "' porque tiene "
                + usuariosConRol + " usuario(s) asociado(s)");

        rolesRepository.deleteById(id);
    }
}
