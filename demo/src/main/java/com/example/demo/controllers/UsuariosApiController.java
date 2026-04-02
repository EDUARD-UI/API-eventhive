package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Estado;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.GlobalController;
import com.example.demo.service.ServiceEstado;
import com.example.demo.service.ServiceRoles;
import com.example.demo.service.ServiceUsuario;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuariosApiController {

    private final ServiceUsuario serviceUsuario;
    private final ServiceRoles serviceRol;
    private final ServiceEstado serviceEstado;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Usuario>>> listarUsuarios() {
        return ResponseEntity.ok(ApiResponse.ok("Usuarios obtenidos", serviceUsuario.obtenerTodosLosUsuarios()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> obtenerUsuario(@PathVariable Long id) {
        Usuario usuario = serviceUsuario.obtenerUsuarioPorId(id);
        if (usuario == null) throw new ResourceNotFoundException("Usuario no encontrado");
        return ResponseEntity.ok(ApiResponse.ok("Usuario obtenido", usuario));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> crearUsuario(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String clave,
            @RequestParam Long rol,
            @RequestParam Long estado) {

        if (usuarioRepository.findByCorreo(correo) != null) {
            throw new BusinessException("El correo ya está registrado");
        }

        Rol rolObj = serviceRol.findById(rol);
        if (rolObj == null) throw new ResourceNotFoundException("El rol especificado no existe");

        Estado estadoObj = serviceEstado.findById(estado);
        if (estadoObj == null) throw new ResourceNotFoundException("El estado especificado no existe");

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setClave(passwordEncoder.encode(clave)); // clave encriptada
        nuevoUsuario.setRol(rolObj);
        nuevoUsuario.setEstado(estadoObj);

        serviceUsuario.crearUsuario(nuevoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Usuario creado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> actualizarUsuario(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam(required = false) String clave,
            @RequestParam Long rol,
            @RequestParam Long estado) {

        Usuario usuarioExistente = serviceUsuario.obtenerUsuarioPorId(id);
        if (usuarioExistente == null) throw new ResourceNotFoundException("El usuario no existe");

        Usuario usuarioConCorreo = usuarioRepository.findByCorreo(correo);
        if (usuarioConCorreo != null && !usuarioConCorreo.getId().equals(id)) {
            throw new BusinessException("El correo ya está registrado por otro usuario");
        }

        Rol rolObj = serviceRol.findById(rol);
        if (rolObj == null) throw new ResourceNotFoundException("El rol especificado no existe");

        Estado estadoObj = serviceEstado.findById(estado);
        if (estadoObj == null) throw new ResourceNotFoundException("El estado especificado no existe");

        usuarioExistente.setNombre(nombre);
        usuarioExistente.setApellido(apellido);
        usuarioExistente.setCorreo(correo);
        usuarioExistente.setTelefono(telefono);
        usuarioExistente.setRol(rolObj);
        usuarioExistente.setEstado(estadoObj);

        if (clave != null && !clave.isBlank()) {
            usuarioExistente.setClave(passwordEncoder.encode(clave));
        }

        serviceUsuario.actualizarUsuario(usuarioExistente);
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado exitosamente"));
    }

    // Obtener perfil del usuario logueado
    @GetMapping("/perfil")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerPerfil() {
        String correo = GlobalController.getCorreoAutenticado();
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        
        if (usuario == null) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        Map<String, Object> perfil = Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "apellido", usuario.getApellido(),
                "correo", usuario.getCorreo(),
                "telefono", usuario.getTelefono() != null ? usuario.getTelefono() : "",
                "rolNombre", usuario.getRol().getNombre(),
                "estado", usuario.getEstado().getNombre()
        );

        return ResponseEntity.ok(ApiResponse.ok("Perfil obtenido", perfil));
    }

    // Actualizar perfil del usuario logueado
    @PutMapping("/perfil")
    public ResponseEntity<ApiResponse<Map<String, Object>>> actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String clave) {

        String correoActual = GlobalController.getCorreoAutenticado();
        Usuario usuarioEnSesion = usuarioRepository.findByCorreo(correoActual);
        
        if (usuarioEnSesion == null) {
            throw new BusinessException("Usuario no encontrado");
        }

        // Validar que el nuevo correo no esté en uso por otro usuario
        if (!correo.equals(usuarioEnSesion.getCorreo())) {
            Usuario usuarioConCorreo = usuarioRepository.findByCorreo(correo);
            if (usuarioConCorreo != null) {
                throw new BusinessException("El correo ya está registrado por otro usuario");
            }
        }

        usuarioEnSesion.setNombre(nombre);
        usuarioEnSesion.setCorreo(correo);
        if (telefono != null && !telefono.isBlank()) {
            usuarioEnSesion.setTelefono(telefono);
        }
        if (clave != null && !clave.isBlank()) {
            usuarioEnSesion.setClave(passwordEncoder.encode(clave));
        }

        serviceUsuario.actualizarUsuario(usuarioEnSesion);
        
        Map<String, Object> perfil = Map.of(
                "id", usuarioEnSesion.getId(),
                "nombre", usuarioEnSesion.getNombre(),
                "apellido", usuarioEnSesion.getApellido(),
                "correo", usuarioEnSesion.getCorreo(),
                "telefono", usuarioEnSesion.getTelefono() != null ? usuarioEnSesion.getTelefono() : "",
                "rolNombre", usuarioEnSesion.getRol().getNombre()
        );

        return ResponseEntity.ok(ApiResponse.ok("Perfil actualizado exitosamente", perfil));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarUsuario(@PathVariable Long id) {
        if (serviceUsuario.obtenerUsuarioPorId(id) == null) {
            throw new ResourceNotFoundException("El usuario no existe");
        }
        serviceUsuario.eliminarUsuario(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado exitosamente"));
    }
}