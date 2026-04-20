package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsuarioDTO;
import com.example.demo.dto.UsuarioSesionDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EventoRepository;
import com.example.demo.repository.RolesRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceUsuario {

    private final UsuarioRepository usuarioRepository;
    private final RolesRepository rolesRepository;
    private final EventoRepository eventoRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public Usuario obtenerUsuarioPorId(String id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Page<Usuario> obtenerTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public UsuarioSesionDTO obtenerSesionDTO(String id) {
        Usuario usuario = obtenerUsuarioPorId(id);
        UsuarioSesionDTO dto = new UsuarioSesionDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCorreo());
        if (usuario.getRol() != null) dto.setRolNombre(usuario.getRol().getNombre());
        return dto;
    }

    public UsuarioDTO obtenerPerfil() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) throw new BusinessException("Usuario no encontrado");
        return usuarioADTO(usuario);
    }

    public UsuarioDTO actualizarPerfil(UsuarioDTO dto) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) throw new BusinessException("Usuario no encontrado");

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());

        usuarioRepository.save(usuario);
        return usuarioADTO(usuario);
    }

    public Object listarEventosDeseados() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) throw new BusinessException("Usuario no encontrado");

        if (usuario.getEventosDeseadosIds() == null || usuario.getEventosDeseadosIds().isEmpty()) {
            return List.of();
        }

        return eventoRepository.findAllById(usuario.getEventosDeseadosIds());
    }

    public void crearUsuario(String nombre, String apellido, String correo, String telefono, String clave, String rolId) {
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new BusinessException("Correo ya registrado");
        }

        Rol rol = rolesRepository.findById(rolId)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no existe"));

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setClave(passwordEncoder.encode(clave));
        usuario.setRol(rol);

        usuarioRepository.save(usuario);
    }

    public void actualizarUsuario(String id, String nombre, String apellido, String telefono) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setTelefono(telefono);
        usuarioRepository.save(usuario);
    }

    public void cambiarContrasena(String id, String claveAnterior, String claveNueva) {
        Usuario usuario = obtenerUsuarioPorId(id);
        if (!passwordEncoder.matches(claveAnterior, usuario.getClave())) {
            throw new BusinessException("Contraseña incorrecta");
        }
        usuario.setClave(passwordEncoder.encode(claveNueva));
        usuarioRepository.save(usuario);
    }

    public void asignarRol(String usuarioId, String rolId) {
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        Rol rol = rolesRepository.findById(rolId)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no existe"));
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(String id) {
        usuarioRepository.deleteById(id);
    }

    public long contar() {
        return usuarioRepository.count();
    }

    private UsuarioDTO usuarioADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCorreo());
        dto.setTelefono(usuario.getTelefono());
        if (usuario.getRol() != null) dto.setRolNombre(usuario.getRol().getNombre());
        return dto;
    }
}