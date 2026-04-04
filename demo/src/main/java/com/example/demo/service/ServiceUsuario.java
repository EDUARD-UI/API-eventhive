package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Estado;
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
    private final ServiceEstado serviceEstado;

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public void crearUsuario(String nombre, String apellido, String correo,
                              String telefono, String clave, Long rolId, Long estadoId) {

        if (obtenerUsuarioPorCorreo(correo) != null)
            throw new BusinessException("El correo ya está registrado");

        Rol rol = serviceRoles.findById(rolId);
        if (rol == null) throw new ResourceNotFoundException("El rol especificado no existe");

        Estado estado = serviceEstado.obtenerEstadoPorId(rolId);
        if (estado == null) throw new ResourceNotFoundException("El estado especificado no existe");

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setApellido(apellido);
        nuevo.setCorreo(correo);
        nuevo.setTelefono(telefono);
        nuevo.setClave(passwordEncoder.encode(clave));
        nuevo.setRol(rol);
        nuevo.setEstado(estado);

        usuarioRepository.save(nuevo);
    }

    public void actualizarUsuario(Long id, String nombre, String apellido, String correo,
                                   String telefono, String clave, Long rolId, Long estadoId) {

        Usuario existente = obtenerUsuarioPorId(id);
        if (existente == null) throw new ResourceNotFoundException("El usuario no existe");

        Usuario conCorreo = obtenerUsuarioPorCorreo(correo);
        if (conCorreo != null && !conCorreo.getId().equals(id))
            throw new BusinessException("El correo ya está registrado por otro usuario");

        Rol rol = serviceRoles.findById(rolId);
        if (rol == null) throw new ResourceNotFoundException("El rol especificado no existe");

        Estado estado = serviceEstado.obtenerEstadoPorId(estadoId);
        if (estado == null) throw new ResourceNotFoundException("El estado especificado no existe");

        existente.setNombre(nombre);
        existente.setApellido(apellido);
        existente.setCorreo(correo);
        existente.setTelefono(telefono);
        existente.setRol(rol);
        existente.setEstado(estado);

        if (clave != null && !clave.isBlank())
            existente.setClave(passwordEncoder.encode(clave));

        usuarioRepository.save(existente);
    }

    public void eliminarUsuario(Long id) {
        if (obtenerUsuarioPorId(id) == null)
            throw new ResourceNotFoundException("El usuario no existe");
        usuarioRepository.deleteById(id);
    }

    //perfil del usuario en sesión (sin tocar rol ni estado)
    public Map<String, Object> obtenerPerfil(Usuario usuario) {
        return Map.of(
                "id",        usuario.getId(),
                "nombre",    usuario.getNombre(),
                "apellido",  usuario.getApellido(),
                "correo",    usuario.getCorreo(),
                "telefono",  usuario.getTelefono() != null ? usuario.getTelefono() : "",
                "rolNombre", usuario.getRol().getNombre(),
                "estado",    usuario.getEstado().getNombre()
        );
    }

    public Map<String, Object> actualizarPerfil(Usuario usuarioEnSesion, String nombre,
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

        usuarioRepository.save(usuarioEnSesion);

        return Map.of(
                "id",        usuarioEnSesion.getId(),
                "nombre",    usuarioEnSesion.getNombre(),
                "apellido",  usuarioEnSesion.getApellido(),
                "correo",    usuarioEnSesion.getCorreo(),
                "telefono",  usuarioEnSesion.getTelefono() != null ? usuarioEnSesion.getTelefono() : "",
                "rolNombre", usuarioEnSesion.getRol().getNombre()
        );
    }
}