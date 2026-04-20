package com.example.demo.converter;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.example.demo.model.Rol;

@Component
public class RolWritingConverter implements Converter<Rol, Document> {

    @Override
    public Document convert(Rol source) {
        if (source == null) {
            return null;
        }

        Document document = new Document();
        
        if (source.getId() != null) {
            document.put("_id", source.getId());
        }
        
        if (source.getNombre() != null) {
            document.put("nombre", source.getNombre());
        }
        
        if (source.getDescripcion() != null) {
            document.put("descripcion", source.getDescripcion());
        }
        
        return document;
    }
}