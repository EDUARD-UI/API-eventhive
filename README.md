EventHive es una aplicación web full-stack para la publicación, descubrimiento y compra de boletos a eventos culturales, musicales y de entretenimiento en Cartagena, Colombia. La arquitectura sigue el patrón clásico de backend MVC + frontend estático, con una API REST como puente entre ambas capas.

Stack tecnológico
Backend

Java 17 + Spring Boot 3.5
Spring MVC (REST controllers), Spring Data JPA, Hibernate
Maven como gestor de dependencias
Lombok para reducir boilerplate
Sesiones HTTP nativas (Jakarta HttpSession) para autenticación
GraphQL incluido en el pom.xml pero no activo en la vista pública

Base de datos

MySQL — base de datos eventhive
Esquema gestionado por Hibernate con ddl-auto=update

Frontend

HTML + JavaScript vanilla (sin framework)
Tailwind CSS vía CDN
SweetAlert2 para alertas
QRCode.js para generación de boletos
Panel del organizador con router SPA