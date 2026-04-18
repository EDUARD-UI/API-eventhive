package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Estado;
import com.example.demo.model.Rol;
import com.example.demo.repository.RolesRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceRoles {

    private final RolesRepository rolesRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServiceEstado serviceEstado;

    public Rol findById(long id) {
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

    public boolean tieneUsuariosAsociados(Long rolId) {
        return usuarioRepository.countByRolId(rolId) > 0;
    }

    //lógica que antes vivía en el controller
    public void crearRol(String nombre, String descripcion, Long estadoId) {
        if (findByNombre(nombre) != null)
            throw new BusinessException("Ya existe un rol con ese nombre");

        Estado estado = serviceEstado.obtenerEstadoPorId(estadoId);
        if (estado == null) throw new ResourceNotFoundException("El estado especificado no existe");

        Rol nuevo = new Rol();
        nuevo.setNombre(nombre);
        nuevo.setDescripcion(descripcion);
        nuevo.setEstado(estado);
        rolesRepository.save(nuevo);
    }

    public void actualizarRol(Long id, String nombre, String descripcion, Long estadoId) {
        Rol existente = findById(id);
        if (existente == null) throw new ResourceNotFoundException("El rol no existe");

        Rol conNombre = findByNombre(nombre);
        if (conNombre != null && !conNombre.getId().equals(id))
            throw new BusinessException("Ya existe otro rol con ese nombre");

        Estado estado = serviceEstado.obtenerEstadoPorId(estadoId);
        if (estado == null) throw new ResourceNotFoundException("El estado especificado no existe");

        existente.setNombre(nombre);
        existente.setDescripcion(descripcion);
        existente.setEstado(estado);
        rolesRepository.save(existente);
    }

    public void eliminarRol(Long id) {
        if (findById(id) == null) throw new ResourceNotFoundException("El rol no existe");

        if (tieneUsuariosAsociados(id))
            throw new BusinessException("No se puede eliminar el rol porque tiene usuarios asociados");

        rolesRepository.deleteById(id);
    }
}