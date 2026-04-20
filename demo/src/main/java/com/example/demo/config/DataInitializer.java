package com.example.demo.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.Categoria;
import com.example.demo.model.Estado;
import com.example.demo.model.Evento;
import com.example.demo.model.Localidad;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (mongoTemplate.collectionExists("usuarios") && 
                mongoTemplate.getCollection("usuarios").countDocuments() > 0) {
                System.out.println("✅ Base de datos ya inicializada");
                return;
            }

            System.out.println("🌱 Inicializando MongoDB...");

            // Roles
            Rol rolCliente = new Rol();
            rolCliente.setNombre("CLIENTE");
            rolCliente.setDescripcion("Usuario regular");

            Rol rolOrganizador = new Rol();
            rolOrganizador.setNombre("ORGANIZADOR");
            rolOrganizador.setDescripcion("Crea eventos");

            Rol rolAdmin = new Rol();
            rolAdmin.setNombre("ADMINISTRADOR");
            rolAdmin.setDescripcion("Control total");

            mongoTemplate.insertAll(List.of(rolCliente, rolOrganizador, rolAdmin));

            // Estados
            Estado estadoPublicado = new Estado();
            estadoPublicado.setNombre("PUBLICADO");
            estadoPublicado.setDescripcion("Evento visible");

            Estado estadoCancelado = new Estado();
            estadoCancelado.setNombre("CANCELADO");
            estadoCancelado.setDescripcion("Evento cancelado");

            mongoTemplate.insertAll(List.of(estadoPublicado, estadoCancelado));

            // Categorías
            Categoria categoriaConcierto = new Categoria();
            categoriaConcierto.setNombre("Concierto");
            categoriaConcierto.setFoto("concierto.jpg");

            Categoria categoriaTeatro = new Categoria();
            categoriaTeatro.setNombre("Teatro");
            categoriaTeatro.setFoto("teatro.jpg");

            mongoTemplate.insertAll(List.of(categoriaConcierto, categoriaTeatro));

            // Usuarios - EMBEBENDO rol directamente
            Usuario eduard = new Usuario();
            eduard.setNombre("Eduard Santiago");
            eduard.setApellido("Tordeciña Faria");
            eduard.setCorreo("eduardestf20@gmail.com");
            eduard.setClave(passwordEncoder.encode("eduard123"));
            eduard.setTelefono("3046139087");
            eduard.setRol(rolAdmin);
            eduard.setEventosDeseadosIds(new ArrayList<>());

            Usuario angie = new Usuario();
            angie.setNombre("Angie Carolina");
            angie.setApellido("Torres Narvaez");
            angie.setCorreo("angiecarloinatorresnarvaez@gmail.com");
            angie.setClave(passwordEncoder.encode("angie123"));
            angie.setTelefono("3046768923");
            angie.setRol(rolAdmin);
            angie.setEventosDeseadosIds(new ArrayList<>());

            Usuario jhonatan = new Usuario();
            jhonatan.setNombre("Jhonatan");
            jhonatan.setApellido("Saez Agamez");
            jhonatan.setCorreo("contacteljohnny@gmail.com");
            jhonatan.setClave(passwordEncoder.encode("jhonatan123"));
            jhonatan.setTelefono("3029991144");
            jhonatan.setRol(rolOrganizador);
            jhonatan.setEventosDeseadosIds(new ArrayList<>());

            mongoTemplate.insertAll(List.of(eduard, angie, jhonatan));

            // Evento con localidades embebidas
            Evento evento = new Evento();
            evento.setTitulo("Concierto Rock 2024");
            evento.setDescripcion("Mejor concierto del año");
            evento.setFoto("rock.jpg");
            evento.setFecha(LocalDate.of(2024, 12, 15));
            evento.setHora(LocalTime.of(20, 0));
            evento.setLugar("Estadio Nacional");
            evento.setEstado(estadoPublicado);
            evento.setCategoria(categoriaConcierto);
            evento.setOrganizador(jhonatan);

            Localidad vip = new Localidad();
            vip.setNombre("VIP");
            vip.setPrecio(new BigDecimal("250.00"));
            vip.setCapacidad(100);
            vip.setDisponibles(100);

            Localidad general = new Localidad();
            general.setNombre("General");
            general.setPrecio(new BigDecimal("80.00"));
            general.setCapacidad(500);
            general.setDisponibles(500);

            evento.setLocalidades(List.of(vip, general));
            evento.setPromociones(new ArrayList<>());

            mongoTemplate.insertAll(List.of(evento));

            System.out.println("✅ Roles creados");
            System.out.println("✅ Estados creados");
            System.out.println("✅ Categorías creadas");
            System.out.println("✅ Usuarios creados");
            System.out.println("✅ Evento creado");
            System.out.println("\n🔐 Credenciales:");
            System.out.println("   📧 eduardestf20@gmail.com / eduard123");
            System.out.println("   📧 angiecarloinatorresnarvaez@gmail.com / angie123");
            System.out.println("   📧 contacteljohnny@gmail.com / jhonatan123\n");
        };
    }
}