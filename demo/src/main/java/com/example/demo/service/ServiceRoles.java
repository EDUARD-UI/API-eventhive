package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Rol;
import com.example.demo.repository.RolesRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceRoles {
    
    private final RolesRepository rolesRepository;
    private final UsuarioRepository usuarioRepository;

    public Rol findById(long id){
        return rolesRepository.findById(id).orElse(null);
    }

    public Rol crearRol(Rol rol) {
        return rolesRepository.save(rol);
    }

    public Rol actualizarRol(Rol rol){
        return rolesRepository.save(rol);
    }

    public List<Rol> obtenerTodosRoles(){
        return rolesRepository.findAll();
    }

    public Rol findByNombre(String nombre) {
        return rolesRepository.findByNombre(nombre);
    }

    public void deleteById(Long id) {
        rolesRepository.deleteById(id);
    }

    public boolean tieneUsuariosAsociados(Long rolId) {
        return usuarioRepository.countByRolId(rolId) > 0;
    }
}
