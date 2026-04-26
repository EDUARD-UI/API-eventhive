package com.example.demo.converter;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import com.example.demo.model.Rol;

@Component
@ReadingConverter
public class RolReadingConverter implements Converter<Document, Rol> {

    @Override
    public Rol convert(Document source) {
        // Manejo de null explícito
        if (source == null) {
            return null;
        }

        try {
            Rol rol = new Rol();
            
            // Obtener ID - puede estar en "_id"
            Object idObj = source.get("_id");
            if (idObj != null) {
                rol.setId(idObj.toString());
            }
            
            // Obtener nombre - validar que exista
            String nombre = source.getString("nombre");
            if (nombre != null && !nombre.isEmpty()) {
                rol.setNombre(nombre);
            } else {
                // Rol sin nombre es inválido
                return null;
            }
            
            // Obtener descripción - opcional
            String descripcion = source.getString("descripcion");
            if (descripcion != null) {
                rol.setDescripcion(descripcion);
            }
            
            return rol;
            
        } catch (Exception e) {
            // Log para debugging
            System.err.println("Error al convertir Document a Rol: " + e.getMessage());
            return null;
        }
    }
}
