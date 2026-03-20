package com.example.demo.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Usuario;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute
    public void agregarDatosGlobales(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuario != null) model.addAttribute("usuarioActual", usuario);
    }

    // Devuelve el usuario en sesión o lanza excepción
    public static Usuario sesionRequerida(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogeado");
        if (u == null) throw new BusinessException("Debe iniciar sesión para continuar");
        return u;
    }

    //valida q haya session logeada y q el rol sea permitido
    public static Usuario rolRequerido(HttpSession session, String... roles) {
        Usuario u = sesionRequerida(session);
        String rolActual = u.getRol().getNombre().toLowerCase();
        for (String rol : roles) {
            if (rolActual.equals(rol.toLowerCase())) return u;
        }
        throw new BusinessException("No tiene permisos para realizar esta acción");
    }

    // valida q el usuario no ingrese a una funcion q no le corresponde
    public static void propietarioRequerido(Long idRecurso, Long idUsuario) {
        if (!idRecurso.equals(idUsuario))
            throw new BusinessException("No autorizado para acceder a este recurso");
    }


    //MANEJO DE EXCEPCIONES GLOBALES
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
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
