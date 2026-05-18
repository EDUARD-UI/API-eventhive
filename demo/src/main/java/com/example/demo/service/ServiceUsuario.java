package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsuarioDTO;
import com.example.demo.dto.UsuarioSesionDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.RolesRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.utils.AuthenticatedUserHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceUsuario {

    private final UsuarioRepository usuarioRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserHelper authHelper;

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

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Page<Usuario> obtenerTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Page<Usuario> buscarPorNombre(String nombre, Pageable pageable) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    public UsuarioSesionDTO obtenerSesionDTO(String correoOId) {
        Usuario usuario = usuarioRepository.findByCorreo(correoOId);

        if (usuario == null) {
            usuario = usuarioRepository.findById(correoOId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        }

        UsuarioSesionDTO dto = new UsuarioSesionDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCorreo());
        dto.setTelefono(usuario.getTelefono());
        dto.setRolNombre(usuario.getRol() != null ? usuario.getRol().getNombre() : "cliente");
        dto.setEsVerificado(usuario.getEsVerificado() != null ? usuario.getEsVerificado() : false);

        return dto;
    }

    @PreAuthorize("isAuthenticated()")
    public UsuarioDTO obtenerPerfil() {
        return usuarioADTO(authHelper.usuarioAutenticado());
    }

    @PreAuthorize("isAuthenticated()")
    public UsuarioDTO actualizarPerfil(UsuarioDTO dto) {
        Usuario usuario = authHelper.usuarioAutenticado();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());
        usuarioRepository.save(usuario);
        return usuarioADTO(usuario);
    }

    @PreAuthorize("isAuthenticated()")
    public Object listarEventosDeseados() {
        Usuario usuario = authHelper.usuarioAutenticado();
        if (usuario.getEventosDeseados() == null || usuario.getEventosDeseados().isEmpty()) {
            return List.of();
        }
        return usuario.getEventosDeseados();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
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
        usuario.setEsVerificado(false);

        usuarioRepository.save(usuario);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
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

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void asignarRol(String usuarioId, String rolId) {
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        Rol rol = rolesRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no existe"));
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
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
        dto.setEsVerificado(usuario.getEsVerificado());
        if (usuario.getRol() != null) {
            dto.setRolNombre(usuario.getRol().getNombre());
        }
        return dto;
    }
}
