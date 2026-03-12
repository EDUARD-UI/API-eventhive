package com.example.demo.exception;

import com.example.demo.model.Usuario;

import jakarta.servlet.http.HttpSession;

public class SesionLogeada {
    
    public Usuario getUsuarioSesion(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuario == null) throw new BusinessException("Usuario no autenticado");
        return usuario;
    }
}
