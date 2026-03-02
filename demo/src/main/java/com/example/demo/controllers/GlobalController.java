package com.example.demo.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.model.Usuario;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute
    public void agregarDatosGlobales(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");
        if (usuario != null) {
            model.addAttribute("usuarioActual", usuario);
        }
    }
}
