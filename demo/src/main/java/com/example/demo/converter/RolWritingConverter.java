package com.example.demo.converter;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import com.example.demo.model.Rol;

@Component
@WritingConverter
public class RolWritingConverter implements Converter<Rol, Document> {

    @Override
    public Document convert(Rol source) {
        // Manejo de null explícito
        if (source == null) {
            return null;
        }

        try {
            Document doc = new Document();
            
            // Guardar ID como _id
            if (source.getId() != null && !source.getId().isEmpty()) {
                doc.put("_id", source.getId());
            }
            
            // Guardar nombre - campo requerido
            if (source.getNombre() != null && !source.getNombre().isEmpty()) {
                doc.put("nombre", source.getNombre());
            } else {
                throw new IllegalArgumentException("Rol debe tener un nombre definido");
            }
            
            // Guardar descripción - campo opcional
            if (source.getDescripcion() != null && !source.getDescripcion().isEmpty()) {
                doc.put("descripcion", source.getDescripcion());
            }
            
            return doc;
            
        } catch (Exception e) {
            System.err.println("Error al convertir Rol a Document: " + e.getMessage());
            throw new RuntimeException("Error en conversión de Rol", e);
        }
    }
}
