package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.example.demo.service.ServiceEstado;
import com.example.demo.service.ServiceRoles;
import com.example.demo.service.ServiceUsuario;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuariosApiController {

    private final ServiceUsuario serviceUsuario;
    private final ServiceRoles serviceRol;
    private final ServiceEstado serviceEstado;
    private final UsuarioRepository usuarioRepository;

    // GET /api/usuarios → listar todos (exlusivo para admin)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Usuario>>> listarUsuarios() {
        return ResponseEntity.ok(ApiResponse.ok("Usuarios obtenidos", serviceUsuario.obtenerTodosLosUsuarios()));
    }

    // GET /api/usuarios/{id} → obtener por id (exlusivo para admin)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> obtenerUsuario(@PathVariable Long id) {
        Usuario usuario = serviceUsuario.obtenerUsuarioPorId(id);
        if (usuario == null) throw new ResourceNotFoundException("Usuario no encontrado");
        return ResponseEntity.ok(ApiResponse.ok("Usuario obtenido", usuario));
    }

    // POST /api/usuarios → crear usuario desde admin
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
        nuevoUsuario.setClave(clave);
        nuevoUsuario.setRol(rolObj);
        nuevoUsuario.setEstado(estadoObj);

        serviceUsuario.crearUsuario(nuevoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Usuario creado exitosamente"));
    }

    // PUT /api/usuarios/{id} → actualizar usuario (exlusivo para admin)
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
        if (clave != null && !clave.isBlank()) usuarioExistente.setClave(clave);

        serviceUsuario.actualizarUsuario(usuarioExistente);
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado exitosamente"));
    }

    // PUT /api/usuarios/perfil → el usuario logueado actualiza sus propios datos personales
    @PutMapping("/perfil")
    public ResponseEntity<ApiResponse<Void>> actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String clave,
            HttpSession session) {

        Usuario usuarioEnSesion = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuarioEnSesion == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Debe iniciar sesión para editar el perfil"));
        }

        // validacion y actualización
        serviceUsuario.actualizarPerfil(usuarioEnSesion.getId(), nombre, correo, telefono, clave);

        // Refrescar sesion con los nuevos datos
        Usuario actualizado = serviceUsuario.obtenerUsuarioPorId(usuarioEnSesion.getId());
        session.setAttribute("usuarioLogeado", actualizado);
        session.setAttribute("usuarioNombre", actualizado.getNombre());
        session.setAttribute("usuarioEmail", actualizado.getCorreo());

        return ResponseEntity.ok(ApiResponse.ok("Perfil actualizado exitosamente"));
    }

    // DELETE /api/usuarios/{id} → eliminar usuario (exlusivo para admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarUsuario(@PathVariable Long id) {
        if (serviceUsuario.obtenerUsuarioPorId(id) == null) {
            throw new ResourceNotFoundException("El usuario no existe");
        }
        serviceUsuario.eliminarUsuario(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado exitosamente"));
    }
}