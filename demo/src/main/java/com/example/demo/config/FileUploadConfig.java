package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${upload.path:uploads/categorias}")
    private String uploadPathCategorias;

    @Value("${upload.path.eventos:uploads/eventos}")
    private String uploadPathEventos;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configurar ruta para categorías
        registry.addResourceHandler("/uploads/categorias/**")
                .addResourceLocations("file:" + uploadPathCategorias + "/");

        // Configurar ruta para eventos
        registry.addResourceHandler("/uploads/eventos/**")
                .addResourceLocations("file:" + uploadPathEventos + "/");
    }
}