package com.example.demo.converter;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.example.demo.model.Rol;

@Component
public class RolReadingConverter implements Converter<Document, Rol> {

    @Override
    public Rol convert(Document source) {
        if (source == null) {
            return null;
        }

        Rol rol = new Rol();
        
        Object idObj = source.get("_id");
        if (idObj != null) {
            rol.setId(idObj.toString());
        }
        
        String nombre = source.getString("nombre");
        if (nombre != null) {
            rol.setNombre(nombre);
        }
        
        String descripcion = source.getString("descripcion");
        if (descripcion != null) {
            rol.setDescripcion(descripcion);
        }
        
        return rol;
    }
}