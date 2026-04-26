package com.example.demo.utils;

import com.example.demo.dto.EventoDTO;
import com.example.demo.model.Evento;

public class MongoSerializationHelper {

    public static EventoDTO eventoADTO(Evento evento) {
        if (evento == null) {
            return null;
        }

        EventoDTO dto = new EventoDTO();
        dto.setId(evento.getId());
        dto.setTitulo(evento.getTitulo());
        dto.setDescripcion(evento.getDescripcion());
        dto.setLugar(evento.getLugar());
        dto.setFoto(evento.getFoto());
        dto.setFecha(evento.getFecha());
        dto.setHora(evento.getHora());

        // Manejar categoría con validación
        if (evento.getCategoria() != null) {
            EventoDTO.Categoria cat = new EventoDTO.Categoria(
                evento.getCategoria().getId(),
                evento.getCategoria().getNombre()
            );
            dto.setCategoria(cat);
        }

        // Manejar estado con validación
        if (evento.getEstado() != null) {
            EventoDTO.Estado est = new EventoDTO.Estado(
                evento.getEstado().getId(),
                evento.getEstado().getNombre()
            );
            dto.setEstado(est);
        }

        return dto;
    }

    public static boolean validarReferenciasCargadas(Object objeto) {
        if (objeto == null) {
            return false;
        }

        try {
            // Intenta acceder a los campos críticos
            if (objeto instanceof Evento) {
                Evento e = (Evento) objeto;
                return e.getCategoria() != null && 
                       e.getEstado() != null && 
                       e.getOrganizador() != null;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void forzarCargaReferencias(Evento evento) {
        if (evento != null) {
            // Forzar acceso a cada campo para cargar datos
            if (evento.getCategoria() != null) {
                evento.getCategoria().getNombre();
            }
            if (evento.getEstado() != null) {
                evento.getEstado().getNombre();
            }
            if (evento.getOrganizador() != null) {
                evento.getOrganizador().getNombre();
            }
        }
    }
}
