EventHive es una aplicación web full-stack para la publicación, descubrimiento y compra de boletos a eventos culturales, musicales y de entretenimiento en Cartagena, Colombia. La arquitectura sigue el patrón clásico de backend MVC + frontend estático, con una API REST como puente entre ambas capas.

Stack tecnológico

Backend
Java 17 + Spring Boot 3.5 + spring security 6
Spring MVC (REST controllers), Spring Data JPA, Hibernate
Maven como gestor de dependencias
Lombok para reducir boilerplate

Base de datos
MySQL---Esquema gestionado por Hibernate

Frontend
HTML + JavaScript vanilla (sin framework)
Tailwind CSS vía CDN
SweetAlert2 para alertas
QRCode.js para generación de boletos
Panel del organizador con router SPA