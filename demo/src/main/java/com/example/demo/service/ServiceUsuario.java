package com.example.demo.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceUsuario {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Actualiza solo los datos personales del usuario logueado (sin tocar rol ni estado)
    public void actualizarPerfil(Long usuarioId, String nombre, String correo,
                                  String telefono, String clave) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));

        Usuario conMismoCorreo = usuarioRepository.findByCorreo(correo);
        if (conMismoCorreo != null && !conMismoCorreo.getId().equals(usuarioId)) {
            throw new BusinessException("El correo ya está registrado por otro usuario");
        }

        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        if (telefono != null) usuario.setTelefono(telefono);

        if (clave != null && !clave.isBlank()) {
            usuario.setClave(passwordEncoder.encode(clave));
        }

        usuarioRepository.save(usuario);
    }
}