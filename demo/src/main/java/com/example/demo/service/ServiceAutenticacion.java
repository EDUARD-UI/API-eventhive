package com.example.demo.service;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.RolesRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceAutenticacion {

    private final UsuarioRepository usuarioRepository;
    private final RolesRepository rolesRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public Authentication autenticar(String correo, String clave) {
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(correo, clave)
        );
    }

    public Map<String, Object> obtenerDatosUsuarioAutenticado(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) throw new BusinessException("Usuario no encontrado");

        return Map.of(
            "id", usuario.getId(),
            "nombre", usuario.getNombre(),
            "apellido", usuario.getApellido(),
            "correo", usuario.getCorreo(),
            "telefono", usuario.getTelefono() != null ? usuario.getTelefono() : "",
            "rolNombre", usuario.getRol().getNombre(),
            "esVerificado", usuario.getEsVerificado() // AGREGADO
        );
    }

    public String obtenerRolUsuario(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) throw new BusinessException("Usuario no encontrado");
        return usuario.getRol().getNombre();
    }

    public void registrarCliente(String nombre, String apellido, String correo, String telefono, String clave) {
        validarRegistro(correo);
        Rol rol = rolesRepository.findByNombre("CLIENTE");
        if (rol == null) throw new BusinessException("Rol CLIENTE no existe");
        usuarioRepository.save(crearUsuario(nombre, apellido, correo, telefono, clave, rol));
    }

    public void registrarOrganizador(String nombre, String apellido, String correo, String telefono, String clave) {
        validarRegistro(correo);
        Rol rol = rolesRepository.findByNombre("ORGANIZADOR");
        if (rol == null) throw new BusinessException("Rol ORGANIZADOR no existe");
        usuarioRepository.save(crearUsuario(nombre, apellido, correo, telefono, clave, rol));
    }

    private Usuario crearUsuario(String nombre, String apellido, String correo, String telefono, String clave, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setClave(passwordEncoder.encode(clave));
        usuario.setRol(rol);
        usuario.setEsVerificado(false);
        return usuario;
    }

    private void validarRegistro(String correo) {
        Usuario existe = usuarioRepository.findByCorreo(correo);
        if (existe != null) throw new BusinessException("Correo ya registrado");
    }
}
