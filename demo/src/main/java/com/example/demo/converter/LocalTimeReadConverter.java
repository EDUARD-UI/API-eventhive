package com.example.demo.converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class LocalTimeReadConverter implements Converter<String, LocalTime> {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    @Override
    public LocalTime convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            // Intentar parsear con formato HH:mm:ss
            return LocalTime.parse(source, FORMATTER);
        } catch (Exception e) {
            try {
                // Intentar parsear con formato por defecto
                return LocalTime.parse(source);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}