package com.example.demo.converter;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import com.example.demo.model.Rol;

public class RolReadingConverter implements Converter<Document, Rol> {

    @Override
    public Rol convert(Document source) {
        if (source == null) return null;

        Rol rol = new Rol();
        rol.setId(source.getString("_id"));
        rol.setNombre(source.getString("nombre"));
        rol.setDescripcion(source.getString("descripcion"));
        return rol;
    }
}