package com.example.demo.security.Auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Sin sesión activa o usuario anónimo
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("No hay sesión activa"));
        }

        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Usuario no encontrado"));
        }

        Map<String, Object> data = Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "apellido", usuario.getApellido(),
                "correo", usuario.getCorreo(),
                "telefono", usuario.getTelefono() != null ? usuario.getTelefono() : "",
                "rolNombre", usuario.getRol().getNombre()
        );

        return ResponseEntity.ok(ApiResponse.ok("Sesión activa", data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @RequestParam String correo,
            @RequestParam String clave,
            HttpSession session) {

        // Spring Security autentica y guarda en SecurityContext
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(correo, clave)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Necesario para que la sesión persista entre requests
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        Usuario user = usuarioRepository.findByCorreo(correo);

        String redirectUrl = switch (user.getRol().getNombre()) {
            case "administrador" ->
                "/administracion/dashboard";
            case "organizador" ->
                "/organizador/dashboard";
            default ->
                "/";
        };

        return ResponseEntity.ok(ApiResponse.ok("Inicio de sesión exitoso",
                Map.of("redirectUrl", redirectUrl,
                        "rol", user.getRol().getNombre())));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok("Sesión cerrada exitosamente"));
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
        nuevoUsuario.setClave(passwordEncoder.encode(clave));
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
        nuevoUsuario.setClave(passwordEncoder.encode(clave));
        nuevoUsuario.setEstado(estadoActivo);
        nuevoUsuario.setRol(rolOrganizador);

        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registro exitoso"));
    }

}
