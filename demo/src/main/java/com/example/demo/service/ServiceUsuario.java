package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceUsuario {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceRoles serviceRoles;

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Page<Usuario> obtenerTodosLosUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Usuario obtenerUsuarioPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    public Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public void crearUsuario(String nombre, String apellido, String correo,
                              String telefono, String clave, String rolId) {

        if (obtenerUsuarioPorCorreo(correo) != null)
            throw new BusinessException("El correo ya está registrado");

        Rol rol = serviceRoles.findById(rolId);
        if (rol == null) throw new ResourceNotFoundException("El rol especificado no existe");

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setApellido(apellido);
        nuevo.setCorreo(correo);
        nuevo.setTelefono(telefono);
        nuevo.setClave(passwordEncoder.encode(clave));
        nuevo.setRol(rol);

        usuarioRepository.save(nuevo);
    }

    public void actualizarUsuario(String id, String nombre, String apellido, String correo,
                                   String telefono, String clave, String rolId) {

        Usuario existente = obtenerUsuarioPorId(id);
        if (existente == null) throw new ResourceNotFoundException("El usuario no existe");

        Usuario conCorreo = obtenerUsuarioPorCorreo(correo);
        if (conCorreo != null && !conCorreo.getId().equals(id))
            throw new BusinessException("El correo ya está registrado por otro usuario");

        Rol rol = serviceRoles.findById(rolId);
        if (rol == null) throw new ResourceNotFoundException("El rol especificado no existe");

        existente.setNombre(nombre);
        existente.setApellido(apellido);
        existente.setCorreo(correo);
        existente.setTelefono(telefono);
        existente.setRol(rol);

        if (clave != null && !clave.isBlank())
            existente.setClave(passwordEncoder.encode(clave));

        usuarioRepository.save(existente);
    }

    public void eliminarUsuario(String id) {
        if (obtenerUsuarioPorId(id) == null)
            throw new ResourceNotFoundException("El usuario no existe");
        usuarioRepository.deleteById(id);
    }

    //actualizar perfil del usuario en sesión (sin tocar rol ni estado)
    public Usuario actualizarPerfil(Usuario usuarioEnSesion, String nombre,
                                     String correo, String telefono, String clave) {
        if (!correo.equals(usuarioEnSesion.getCorreo())) {
            Usuario conCorreo = obtenerUsuarioPorCorreo(correo);
            if (conCorreo != null)
                throw new BusinessException("El correo ya está registrado por otro usuario");
        }

        usuarioEnSesion.setNombre(nombre);
        usuarioEnSesion.setCorreo(correo);

        if (telefono != null && !telefono.isBlank())
            usuarioEnSesion.setTelefono(telefono);

        if (clave != null && !clave.isBlank())
            usuarioEnSesion.setClave(passwordEncoder.encode(clave));

        return usuarioRepository.save(usuarioEnSesion);
    }
}