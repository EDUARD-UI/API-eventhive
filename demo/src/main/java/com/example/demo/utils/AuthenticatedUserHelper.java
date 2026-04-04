package com.example.demo.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.Usuario;
import com.example.demo.service.ServiceUsuario;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserHelper {

    private final ServiceUsuario serviceUsuario;

    public String getCorreoAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken)
            throw new BusinessException("Debe iniciar sesión para continuar");
        return auth.getName();
    }

    public Usuario usuarioAutenticado() {
        String correo = getCorreoAutenticado();
        Usuario usuario = serviceUsuario.obtenerUsuarioPorCorreo(correo);
        if (usuario == null)
            throw new BusinessException("Usuario no encontrado en la base de datos");
        return usuario;
    }
}
