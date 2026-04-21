package com.example.demo.security.Auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.service.ServiceAutenticacion;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {

    private final ServiceAutenticacion serviceAutenticacion;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("No hay sesión activa"));
        }

        Map<String, Object> datos = serviceAutenticacion.obtenerDatosUsuarioAutenticado(auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Sesión activa", datos));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            @RequestParam String correo,
            @RequestParam String clave,
            HttpSession session) {
        try {
            Authentication auth = serviceAutenticacion.autenticar(correo, clave);
            SecurityContextHolder.getContext().setAuthentication(auth);
            session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
            );
            return ResponseEntity.ok(ApiResponse.ok("Login exitoso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Credenciales inválidas"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.ok("Sesión cerrada"));
    }

    @PostMapping("/registrar-cliente")
    public ResponseEntity<ApiResponse<Void>> registrarCliente(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String clave) {
        serviceAutenticacion.registrarCliente(nombre, apellido, correo, telefono, clave);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Registro exitoso"));
    }

    @PostMapping("/registrar-organizador")
    public ResponseEntity<ApiResponse<Void>> registrarOrganizador(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String clave) {
        serviceAutenticacion.registrarOrganizador(nombre, apellido, correo, telefono, clave);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Registro exitoso"));
    }
}
