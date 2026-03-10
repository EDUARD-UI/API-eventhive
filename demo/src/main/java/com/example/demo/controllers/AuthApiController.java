package com.example.demo.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Estado;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EstadoRepository;
import com.example.demo.repository.RolesRepository;
import com.example.demo.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UsuarioRepository usuarioRepository;
    private final RolesRepository rolRepository;
    private final EstadoRepository estadoRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @RequestParam String correo,
            @RequestParam String clave,
            HttpSession session) {

        Usuario user = usuarioRepository.findByCorreoAndClave(correo, clave);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Correo o contraseña incorrectos"));
        }

        session.setAttribute("usuarioLogeado", user);
        session.setAttribute("usuarioID", user.getId());
        session.setAttribute("usuarioNombre", user.getNombre());
        session.setAttribute("usuarioEmail", user.getCorreo());
        session.setAttribute("usuarioTelefono", user.getTelefono());
        session.setAttribute("rolUsuario", user.getRol().getNombre());
        session.setAttribute("estadoUsuario", user.getEstado().getNombre());
        session.setAttribute("isLoggedIn", true);
        session.setMaxInactiveInterval(30 * 60);

        String redirectUrl = switch (user.getRol().getNombre()) {
            case "administrador" -> "/administracion/dashboard";
            case "organizador"   -> "/organizador/dashboard";
            case "cliente"       -> "/";
            default              -> "/login";
        };

        return ResponseEntity.ok(ApiResponse.ok("Inicio de sesión exitoso",
                Map.of("redirectUrl", redirectUrl, "rol", user.getRol().getNombre())));
    }

    @PostMapping("/registrar-cliente")
    public ResponseEntity<ApiResponse<Void>> registrarCliente(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String clave) {

        if (usuarioRepository.findByCorreo(correo) != null) {
            throw new BusinessException("El correo ya está registrado");
        }

        Estado estadoActivo = estadoRepository.findByNombre("registro activo");
        Rol rolCliente = rolRepository.findByNombre("cliente");

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setClave(clave);
        nuevoUsuario.setEstado(estadoActivo);
        nuevoUsuario.setRol(rolCliente);

        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registro exitoso"));
    }

    @PostMapping("/registrar-organizador")
    public ResponseEntity<ApiResponse<Void>> registrarOrganizador(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String clave) {

        if (usuarioRepository.findByCorreo(correo) != null) {
            throw new BusinessException("El correo ya está registrado");
        }

        Estado estadoActivo = estadoRepository.findByNombre("registro activo");
        Rol rolOrganizador = rolRepository.findByNombre("organizador");

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setClave(clave);
        nuevoUsuario.setEstado(estadoActivo);
        nuevoUsuario.setRol(rolOrganizador);

        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registro exitoso"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok("Sesión cerrada exitosamente"));
    }
}