package com.example.demo.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
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
            // Verificar si ya hay datos
            if (mongoTemplate.collectionExists("usuarios") && mongoTemplate.getCollection("usuarios").countDocuments() > 0) {
                System.out.println("✅ Datos ya existen, omitiendo inicialización");
                return;
            }

            System.out.println("🌱 Inicializando datos en MongoDB...");

            // ============================================
            // 1. Crear Roles
            // ============================================
            Rol rolCliente = new Rol();
            rolCliente.setNombre("CLIENTE");
            rolCliente.setDescripcion("Usuario regular que puede comprar tiquetes, ver eventos y dejar valoraciones");

            Rol rolOrganizador = new Rol();
            rolOrganizador.setNombre("ORGANIZADOR");
            rolOrganizador.setDescripcion("Crea y gestiona eventos, puede ver reportes de ventas de sus eventos");

            Rol rolAdmin = new Rol();
            rolAdmin.setNombre("ADMINISTRADOR");
            rolAdmin.setDescripcion("Control total del sistema, gestiona usuarios, eventos y configuraciones globales");

            mongoTemplate.insertAll(List.of(rolCliente, rolOrganizador, rolAdmin));
            System.out.println("✅ Roles creados");

            // ============================================
            // 2. Crear Estados
            // ============================================
            Estado estadoBorrador = new Estado();
            estadoBorrador.setNombre("BORRADOR");
            estadoBorrador.setDescripcion("Evento en creación");

            Estado estadoPublicado = new Estado();
            estadoPublicado.setNombre("PUBLICADO");
            estadoPublicado.setDescripcion("Evento visible para todos");

            Estado estadoCancelado = new Estado();
            estadoCancelado.setNombre("CANCELADO");
            estadoCancelado.setDescripcion("Evento cancelado");

            Estado estadoFinalizado = new Estado();
            estadoFinalizado.setNombre("FINALIZADO");
            estadoFinalizado.setDescripcion("Evento ya realizado");

            mongoTemplate.insertAll(List.of(estadoBorrador, estadoPublicado, estadoCancelado, estadoFinalizado));
            System.out.println("✅ Estados creados");

            // ============================================
            // 3. Crear Categorías
            // ============================================
            Categoria categoriaConcierto = new Categoria();
            categoriaConcierto.setNombre("Concierto");
            categoriaConcierto.setFoto("concierto.jpg");

            Categoria categoriaTeatro = new Categoria();
            categoriaTeatro.setNombre("Teatro");
            categoriaTeatro.setFoto("teatro.jpg");

            Categoria categoriaDeportes = new Categoria();
            categoriaDeportes.setNombre("Deportes");
            categoriaDeportes.setFoto("deportes.jpg");

            Categoria categoriaConferencia = new Categoria();
            categoriaConferencia.setNombre("Conferencia");
            categoriaConferencia.setFoto("conferencia.jpg");

            mongoTemplate.insertAll(List.of(categoriaConcierto, categoriaTeatro, categoriaDeportes, categoriaConferencia));
            System.out.println("✅ Categorías creadas");

            // ============================================
            // 4. Crear Usuarios (TUS 4 USUARIOS)
            // ============================================
            
            // Usuario 1: Eduard Santiago - ADMINISTRADOR
            Usuario eduard = new Usuario();
            eduard.setNombre("Eduard Santiago");
            eduard.setApellido("Tordeciña Faria");
            eduard.setCorreo("eduardestf20@gmail.com");
            eduard.setClave(passwordEncoder.encode("eduard123"));
            eduard.setTelefono("3046139087");
            eduard.setRol(rolAdmin);
            eduard.setEventosDeseadosIds(List.of());

            // Usuario 2: Angie Carolina - ADMINISTRADOR
            Usuario angie = new Usuario();
            angie.setNombre("Angie Carolina");
            angie.setApellido("Torres Narvaez");
            angie.setCorreo("angiecarloinatorresnarvaez@gmail.com");
            angie.setClave(passwordEncoder.encode("angie123"));
            angie.setTelefono("3046768923");
            angie.setRol(rolAdmin);
            angie.setEventosDeseadosIds(List.of());

            // Usuario 3: Jhonatan - ORGANIZADOR
            Usuario jhonatan = new Usuario();
            jhonatan.setNombre("Jhonatan");
            jhonatan.setApellido("Saez Agamez");
            jhonatan.setCorreo("contacteljohnny@gmail.com");
            jhonatan.setClave(passwordEncoder.encode("jhonatan123"));
            jhonatan.setTelefono("3029991144");
            jhonatan.setRol(rolOrganizador);
            jhonatan.setEventosDeseadosIds(List.of());

            // Usuario 4: Alejandra Sofia - ORGANIZADOR
            Usuario alejandra = new Usuario();
            alejandra.setNombre("Alejandra Sofia");
            alejandra.setApellido("Bermudez Mora");
            alejandra.setCorreo("alebermudezmora@gmail.com");
            alejandra.setClave(passwordEncoder.encode("alejandra123"));
            alejandra.setTelefono("3008972239");
            alejandra.setRol(rolOrganizador);
            alejandra.setEventosDeseadosIds(List.of());

            mongoTemplate.insertAll(List.of(eduard, angie, jhonatan, alejandra));
            System.out.println("✅ 4 Usuarios creados:");
            System.out.println("   - Eduard Santiago (ADMINISTRADOR)");
            System.out.println("   - Angie Carolina (ADMINISTRADOR)");
            System.out.println("   - Jhonatan (ORGANIZADOR)");
            System.out.println("   - Alejandra Sofia (ORGANIZADOR)");

            // ============================================
            // 5. Crear Eventos de ejemplo
            // ============================================
            
            // Evento 1: Concierto de Rock (creado por Jhonatan)
            Evento evento1 = new Evento();
            evento1.setTitulo("Concierto de Rock 2024");
            evento1.setDescripcion("El mejor concierto de rock del año con bandas internacionales");
            evento1.setFoto("rock-concert.jpg");
            evento1.setFecha(LocalDate.of(2024, 12, 15));
            evento1.setHora(LocalTime.of(20, 0));
            evento1.setLugar("Estadio Nacional");
            evento1.setEstado(estadoPublicado);
            evento1.setCategoria(categoriaConcierto);
            evento1.setOrganizador(jhonatan);

            Localidad vip1 = new Localidad();
            vip1.setNombre("VIP");
            vip1.setPrecio(new BigDecimal("250.00"));
            vip1.setCapacidad(100);
            vip1.setDisponibles(100);

            Localidad general1 = new Localidad();
            general1.setNombre("General");
            general1.setPrecio(new BigDecimal("80.00"));
            general1.setCapacidad(500);
            general1.setDisponibles(500);

            Localidad platea1 = new Localidad();
            platea1.setNombre("Platea");
            platea1.setPrecio(new BigDecimal("150.00"));
            platea1.setCapacidad(200);
            platea1.setDisponibles(200);

            evento1.setLocalidades(List.of(vip1, general1, platea1));
            evento1.setPromociones(List.of());
            evento1.setValoraciones(List.of());

            // Evento 2: Obra de Teatro (creado por Alejandra)
            Evento evento2 = new Evento();
            evento2.setTitulo("Hamlet - Obra de Teatro");
            evento2.setDescripcion("La clásica obra de Shakespeare en una producción moderna");
            evento2.setFoto("hamlet.jpg");
            evento2.setFecha(LocalDate.of(2024, 11, 20));
            evento2.setHora(LocalTime.of(19, 30));
            evento2.setLugar("Teatro Municipal");
            evento2.setEstado(estadoPublicado);
            evento2.setCategoria(categoriaTeatro);
            evento2.setOrganizador(alejandra);

            Localidad vip2 = new Localidad();
            vip2.setNombre("VIP");
            vip2.setPrecio(new BigDecimal("120.00"));
            vip2.setCapacidad(50);
            vip2.setDisponibles(50);

            Localidad general2 = new Localidad();
            general2.setNombre("General");
            general2.setPrecio(new BigDecimal("60.00"));
            general2.setCapacidad(300);
            general2.setDisponibles(300);

            evento2.setLocalidades(List.of(vip2, general2));
            evento2.setPromociones(List.of());
            evento2.setValoraciones(List.of());

            // Evento 3: Final de Fútbol (creado por Jhonatan)
            Evento evento3 = new Evento();
            evento3.setTitulo("Final del Campeonato Nacional");
            evento3.setDescripcion("La gran final del fútbol nacional");
            evento3.setFoto("final-futbol.jpg");
            evento3.setFecha(LocalDate.of(2024, 10, 5));
            evento3.setHora(LocalTime.of(15, 0));
            evento3.setLugar("Estadio Olímpico");
            evento3.setEstado(estadoPublicado);
            evento3.setCategoria(categoriaDeportes);
            evento3.setOrganizador(jhonatan);

            Localidad preferencial = new Localidad();
            preferencial.setNombre("Preferencial");
            preferencial.setPrecio(new BigDecimal("180.00"));
            preferencial.setCapacidad(200);
            preferencial.setDisponibles(200);

            Localidad general3 = new Localidad();
            general3.setNombre("General");
            general3.setPrecio(new BigDecimal("70.00"));
            general3.setCapacidad(800);
            general3.setDisponibles(800);

            evento3.setLocalidades(List.of(preferencial, general3));
            evento3.setPromociones(List.of());
            evento3.setValoraciones(List.of());

            mongoTemplate.insertAll(List.of(evento1, evento2, evento3));
            System.out.println("✅ 3 Eventos de ejemplo creados con localidades embebidas");

            // ============================================
            // 6. Resumen final
            // ============================================
            System.out.println("\n🎉 ========== INICIALIZACIÓN COMPLETADA ==========");
            System.out.println("📊 Resumen de datos creados:");
            System.out.println("   - Roles: 3 (CLIENTE, ORGANIZADOR, ADMINISTRADOR)");
            System.out.println("   - Estados: 4 (BORRADOR, PUBLICADO, CANCELADO, FINALIZADO)");
            System.out.println("   - Categorías: 4 (Concierto, Teatro, Deportes, Conferencia)");
            System.out.println("   - Usuarios: 4 (2 Administradores, 2 Organizadores)");
            System.out.println("   - Eventos: 3 (con localidades embebidas)");
            System.out.println("\n🔐 Credenciales de acceso:");
            System.out.println("   📧 eduardestf20@gmail.com / eduard123 (ADMINISTRADOR)");
            System.out.println("   📧 angiecarloinatorresnarvaez@gmail.com / angie123 (ADMINISTRADOR)");
            System.out.println("   📧 contacteljohnny@gmail.com / jhonatan123 (ORGANIZADOR)");
            System.out.println("   📧 alebermudezmora@gmail.com / alejandra123 (ORGANIZADOR)");
            System.out.println("=================================================\n");
        };
    }
}