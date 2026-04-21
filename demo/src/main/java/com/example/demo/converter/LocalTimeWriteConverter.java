package com.example.demo.converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class LocalTimeWriteConverter implements Converter<LocalTime, String> {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    @Override
    public String convert(LocalTime source) {
        if (source == null) {
            return null;
        }
        return source.format(FORMATTER);
    }
}