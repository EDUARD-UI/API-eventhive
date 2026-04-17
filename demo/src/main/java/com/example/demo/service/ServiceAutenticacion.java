package com.example.demo.service;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.Estado;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceAutenticacion {

    private final ServiceUsuario serviceUsuario;
    private final ServiceRoles serviceRoles;
    private final ServiceEstado serviceEstado;
    private final AuthenticationManager authenticationManager;

    //autenticar usuario
    public Authentication autenticar(String correo, String clave) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(correo, clave)
        );
    }

    //datos de usuario autenticado
    public Map<String, Object> obtenerDatosUsuarioAutenticado(String correo) {
        Usuario usuario = serviceUsuario.obtenerUsuarioPorCorreo(correo);

        if (usuario == null) {
            throw new BusinessException("Usuario no encontrado");
        }

        return Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "apellido", usuario.getApellido(),
                "correo", usuario.getCorreo(),
                "telefono", usuario.getTelefono() != null ? usuario.getTelefono() : "",
                "rolNombre", usuario.getRol().getNombre()
        );
    }

    //obtener el rol del usuario
    public String obtenerRolUsuario(String correo) {
        Usuario usuario = serviceUsuario.obtenerUsuarioPorCorreo(correo);

        if (usuario == null) {
            throw new BusinessException("Usuario no encontrado");
        }

        return usuario.getRol().getNombre();
    }

    //Registra un nuevo cliente
    public void registrarCliente(String nombre, String apellido, String correo,
                                  String telefono, String clave) {
        validarRegistro(correo);

        Estado estadoActivo = serviceEstado.findByNombre("registro activo");
        Rol rolCliente = serviceRoles.findByNombre("cliente");

        serviceUsuario.crearUsuario(nombre, apellido, correo, telefono, clave, estadoActivo.getId(), rolCliente.getId());
    }

    //registra un nuevo organizador
    public void registrarOrganizador(String nombre, String apellido, String correo,
                                      String telefono, String clave) {
        validarRegistro(correo);

        Estado estadoActivo = serviceEstado.findByNombre("registro activo");
        Rol rolOrganizador = serviceRoles.findByNombre("organizador");

        serviceUsuario.crearUsuario(nombre, apellido, correo, telefono, clave, estadoActivo.getId(), rolOrganizador.getId());
    }

    //validar que el correo no este registrado
    private void validarRegistro(String correo) {
        Usuario usuarioExistente = serviceUsuario.obtenerUsuarioPorCorreo(correo);
        if (usuarioExistente != null) {
            throw new BusinessException("El correo ya está registrado");
        }
    }
}
