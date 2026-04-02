package com.example.demo.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@ControllerAdvice
public class GlobalController {

    // Obtiene el usuario autenticado desde Spring Security
    public static String getCorreoAutenticado() {
        Authentication auth = SecurityContextHolder
            .getContext()
            .getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            throw new BusinessException("Debe iniciar sesión para continuar");
        }
        return auth.getName(); // getName() devuelve el correo (username)
    }

    // Reemplaza sesionRequerida — ahora usa Spring Security
    public Usuario sesionRequerida(UsuarioRepository repo) {
        String correo = getCorreoAutenticado();
        Usuario u = repo.findByCorreo(correo);
        if (u == null) throw new BusinessException(
            "Usuario autenticado no encontrado en BD");
        return u;
    }

    // Valida rol usando Spring Security + BD
    public static Usuario rolRequerido(
            UsuarioRepository repo, String... roles) {

        String correo = getCorreoAutenticado();
        Usuario u = repo.findByCorreo(correo);
        if (u == null) throw new BusinessException("Usuario no encontrado");

        String rolActual = u.getRol().getNombre().toLowerCase();
        for (String rol : roles) {
            if (rolActual.equals(rol.toLowerCase())) return u;
        }
        throw new BusinessException(
            "No tiene permisos para realizar esta acción");
    }

    // Manejo de excepciones globales
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleBusiness(
            BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor"));
    }
}