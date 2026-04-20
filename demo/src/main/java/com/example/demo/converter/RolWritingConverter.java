package com.example.demo.converter;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import com.example.demo.model.Rol;

public class RolWritingConverter implements Converter<Rol, Document> {

    @Override
    public Document convert(Rol source) {
        if (source == null) return null;

        Document doc = new Document();
        if (source.getId() != null) doc.put("_id", source.getId());
        if (source.getNombre() != null) doc.put("nombre", source.getNombre());
        if (source.getDescripcion() != null) doc.put("descripcion", source.getDescripcion());
        return doc;
    }
}