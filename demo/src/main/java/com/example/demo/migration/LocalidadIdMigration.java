package com.example.demo.migration;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.example.demo.model.Evento;
import com.example.demo.model.Localidad;
import com.example.demo.repository.EventoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Migración única: asigna un UUID a cada Localidad embebida en Evento
 * que tenga id null. Se ejecuta solo con el perfil "migration" activo:
 *
 *   java -jar app.jar --spring.profiles.active=migration
 *
 * También se puede copiar el bloque fixLocalidadesNulas() al arranque
 * normal de la app durante el deploy de esta versión y luego quitarlo.
 */
@Slf4j
@Configuration
@Profile("migration")
@RequiredArgsConstructor
public class LocalidadIdMigration {

    private final EventoRepository eventoRepository;

    @Bean
    public CommandLineRunner migrarLocalidadesSinId() {
        return args -> {
            log.info("=== Iniciando migración de localidades sin id ===");
            int eventosActualizados = 0;
            int localidadesActualizadas = 0;

            for (Evento evento : eventoRepository.findAll()) {
                boolean modificado = false;

                for (Localidad localidad : evento.getLocalidades()) {
                    if (localidad.getId() == null || localidad.getId().isBlank()) {
                        String nuevoId = UUID.randomUUID().toString();
                        localidad.setId(nuevoId);
                        log.info("  Evento '{}' → Localidad '{}' asignada id: {}",
                            evento.getTitulo(), localidad.getNombre(), nuevoId);
                        localidadesActualizadas++;
                        modificado = true;
                    }
                }

                if (modificado) {
                    eventoRepository.save(evento);
                    eventosActualizados++;
                }
            }

            log.info("=== Migración completada: {} eventos, {} localidades actualizadas ===",
                eventosActualizados, localidadesActualizadas);
        };
    }
}
