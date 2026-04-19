package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final RolesRepository rolesRepository;
    private final UsuarioRepository usuarioRepository;

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
        if (findByNombre(nombre) != null)
            throw new BusinessException("Ya existe un rol con ese nombre");

        Rol nuevo = new Rol();
        nuevo.setNombre(nombre);
        nuevo.setDescripcion(descripcion);
        rolesRepository.save(nuevo);
    }

    public void actualizarRol(String id, String nombre, String descripcion) {
        Rol existente = findById(id);
        if (existente == null) throw new ResourceNotFoundException("El rol no existe");

        Rol conNombre = findByNombre(nombre);
        if (conNombre != null && !conNombre.getId().equals(id))
            throw new BusinessException("Ya existe otro rol con ese nombre");

        existente.setNombre(nombre);
        existente.setDescripcion(descripcion);
        rolesRepository.save(existente);
    }

    public void eliminarRol(String id) {
        if (findById(id) == null) throw new ResourceNotFoundException("El rol no existe");

        if (tieneUsuariosAsociados(id))
            throw new BusinessException("No se puede eliminar el rol porque tiene usuarios asociados");

        rolesRepository.deleteById(id);
    }
}
