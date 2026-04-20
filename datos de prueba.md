# crear la clase DataInitializer con este codigo y conectar a la bd "eventHive" con solo correr el proyecto se inician los datos, hacer al menos 2 o 3 veces para verificar q se iniciaron

package com.example.demo.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.Categoria;
import com.example.demo.model.Compra;
import com.example.demo.model.Estado;
import com.example.demo.model.Evento;
import com.example.demo.model.ItemCompra;
import com.example.demo.model.Localidad;
import com.example.demo.model.Promocion;
import com.example.demo.model.Rol;
import com.example.demo.model.Tiquete;
import com.example.demo.model.Usuario;
import com.example.demo.model.Valoracion;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (mongoTemplate.collectionExists("usuarios")
                    && mongoTemplate.getCollection("usuarios").countDocuments() > 0) {
                System.out.println("✅ Base de datos ya inicializada");
                return;
            }

            System.out.println("🌱 Inicializando MongoDB...");

            //ROLES
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

            //ESTADOS
            Estado estadoPublicado = new Estado();
            estadoPublicado.setNombre("PUBLICADO");
            estadoPublicado.setDescripcion("Evento visible");

            Estado estadoCancelado = new Estado();
            estadoCancelado.setNombre("CANCELADO");
            estadoCancelado.setDescripcion("Evento cancelado");

            mongoTemplate.insertAll(List.of(estadoPublicado, estadoCancelado));

            //CATEGORÍAS
            Categoria categoriaConcierto = new Categoria();
            categoriaConcierto.setNombre("Concierto");
            categoriaConcierto.setFoto("concierto.jpg");

            Categoria categoriaTeatro = new Categoria();
            categoriaTeatro.setNombre("Teatro");
            categoriaTeatro.setFoto("teatro.jpg");

            Categoria categoriaFestival = new Categoria();
            categoriaFestival.setNombre("Festival");
            categoriaFestival.setFoto("festival.jpg");

            mongoTemplate.insertAll(List.of(categoriaConcierto, categoriaTeatro, categoriaFestival));

            //USUARIOS
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

            Usuario laura = new Usuario();
            laura.setNombre("Laura");
            laura.setApellido("Mendez Ruiz");
            laura.setCorreo("laura.organizadora@gmail.com");
            laura.setClave(passwordEncoder.encode("laura123"));
            laura.setTelefono("3105551234");
            laura.setRol(rolOrganizador);
            laura.setEventosDeseadosIds(new ArrayList<>());

            Usuario carlos = new Usuario();
            carlos.setNombre("Carlos");
            carlos.setApellido("Ramirez Soto");
            carlos.setCorreo("carlos.ramirez@gmail.com");
            carlos.setClave(passwordEncoder.encode("carlos123"));
            carlos.setTelefono("3157778899");
            carlos.setRol(rolCliente);
            carlos.setEventosDeseadosIds(new ArrayList<>());

            Usuario maria = new Usuario();
            maria.setNombre("Maria");
            maria.setApellido("Fernandez Lopez");
            maria.setCorreo("maria.fernandez@gmail.com");
            maria.setClave(passwordEncoder.encode("maria123"));
            maria.setTelefono("3128887766");
            maria.setRol(rolCliente);
            maria.setEventosDeseadosIds(new ArrayList<>());

            mongoTemplate.insertAll(List.of(eduard, angie, jhonatan, laura, carlos, maria));

            //EVENTOS
            // Evento 1 - Concierto Rock
            Evento evento1 = new Evento();
            evento1.setTitulo("Concierto Rock 2024");
            evento1.setDescripcion("Mejor concierto del año");
            evento1.setFoto("rock.jpg");
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

            evento1.setLocalidades(List.of(vip1, general1));
            mongoTemplate.save(evento1);

            // Evento 2 - Festival de Verano
            Evento evento2 = new Evento();
            evento2.setTitulo("Festival de Verano 2025");
            evento2.setDescripcion("3 días de música y diversión bajo el sol");
            evento2.setFoto("festival_verano.jpg");
            evento2.setFecha(LocalDate.of(2025, 1, 20));
            evento2.setHora(LocalTime.of(14, 0));
            evento2.setLugar("Parque Simon Bolivar");
            evento2.setEstado(estadoPublicado);
            evento2.setCategoria(categoriaFestival);
            evento2.setOrganizador(laura);

            Localidad platino = new Localidad();
            platino.setNombre("Platino");
            platino.setPrecio(new BigDecimal("400.00"));
            platino.setCapacidad(200);
            platino.setDisponibles(200);

            Localidad preferencial = new Localidad();
            preferencial.setNombre("Preferencial");
            preferencial.setPrecio(new BigDecimal("180.00"));
            preferencial.setCapacidad(800);
            preferencial.setDisponibles(800);

            evento2.setLocalidades(List.of(platino, preferencial));
            mongoTemplate.save(evento2);

            // Evento 3 - Obra de Teatro
            Evento evento3 = new Evento();
            evento3.setTitulo("Hamlet - Obra Clásica");
            evento3.setDescripcion("Presentación especial de la obra de Shakespeare");
            evento3.setFoto("hamlet.jpg");
            evento3.setFecha(LocalDate.of(2025, 2, 10));
            evento3.setHora(LocalTime.of(19, 30));
            evento3.setLugar("Teatro Colón");
            evento3.setEstado(estadoPublicado);
            evento3.setCategoria(categoriaTeatro);
            evento3.setOrganizador(jhonatan);

            Localidad palco = new Localidad();
            palco.setNombre("Palco");
            palco.setPrecio(new BigDecimal("120.00"));
            palco.setCapacidad(50);
            palco.setDisponibles(50);

            Localidad platea = new Localidad();
            platea.setNombre("Platea");
            platea.setPrecio(new BigDecimal("60.00"));
            platea.setCapacidad(300);
            platea.setDisponibles(300);

            evento3.setLocalidades(List.of(palco, platea));
            mongoTemplate.save(evento3);

            // Evento 4 - Festival Electrónica
            Evento evento4 = new Evento();
            evento4.setTitulo("Electro Night Fest");
            evento4.setDescripcion("Los mejores DJs internacionales en una noche");
            evento4.setFoto("electro.jpg");
            evento4.setFecha(LocalDate.of(2025, 3, 5));
            evento4.setHora(LocalTime.of(22, 0));
            evento4.setLugar("Centro de Eventos");
            evento4.setEstado(estadoPublicado);
            evento4.setCategoria(categoriaFestival);
            evento4.setOrganizador(laura);

            Localidad backstage = new Localidad();
            backstage.setNombre("Backstage");
            backstage.setPrecio(new BigDecimal("350.00"));
            backstage.setCapacidad(80);
            backstage.setDisponibles(80);

            Localidad pista = new Localidad();
            pista.setNombre("Pista");
            pista.setPrecio(new BigDecimal("100.00"));
            pista.setCapacidad(1000);
            pista.setDisponibles(1000);

            evento4.setLocalidades(List.of(backstage, pista));
            mongoTemplate.save(evento4);

            // PROMOCIONES 
            Promocion promo1 = new Promocion();
            promo1.setDescripcion("20% de descuento en Festival de Verano");
            promo1.setDescuento(new BigDecimal("20.00"));
            promo1.setFechaInicio(LocalDate.of(2024, 12, 1));
            promo1.setFechaFinal(LocalDate.of(2025, 1, 15));
            promo1.setEvento(evento2);

            Promocion promo2 = new Promocion();
            promo2.setDescripcion("10% de descuento en Concierto Rock");
            promo2.setDescuento(new BigDecimal("10.00"));
            promo2.setFechaInicio(LocalDate.of(2024, 11, 1));
            promo2.setFechaFinal(LocalDate.of(2024, 12, 10));
            promo2.setEvento(evento1);

            // Asignar promociones a eventos
            evento1.setPromociones(List.of(promo2));
            evento2.setPromociones(List.of(promo1));
            evento3.setPromociones(new ArrayList<>());
            evento4.setPromociones(new ArrayList<>());

            mongoTemplate.save(evento1);
            mongoTemplate.save(evento2);
            mongoTemplate.insert(promo1);
            mongoTemplate.insert(promo2);

            // Compra 1 - Carlos compra 2 tickets VIP para evento1
            ItemCompra item1 = new ItemCompra();
            item1.setEventoId(evento1.getId());
            item1.setLocalidadId(vip1.getId());
            item1.setCantidad(2);
            item1.setPrecioUnitario(new BigDecimal("250.00"));

            Compra compra1 = new Compra();
            compra1.setCliente(carlos);
            compra1.setFechaCompra(LocalDateTime.of(2024, 11, 20, 10, 30));
            compra1.setTotal(new BigDecimal("500.00"));
            compra1.setMetodoPago("TARJETA_CREDITO");
            compra1.setItems(List.of(item1));

            mongoTemplate.save(compra1);

            // Generar tiquetes para compra1
            for (int i = 0; i < item1.getCantidad(); i++) {
                Tiquete tiquete = new Tiquete();
                tiquete.setCodigoQR(UUID.randomUUID().toString());
                tiquete.setLocalidadId(vip1.getId());
                tiquete.setEventoId(evento1.getId());
                mongoTemplate.save(tiquete);
            }

            // Actualizar disponibles de vip1
            vip1.setDisponibles(vip1.getDisponibles() - 2);
            mongoTemplate.save(evento1);

            // Compra 2 - Carlos compra 3 tickets Preferencial para evento2
            ItemCompra item2 = new ItemCompra();
            item2.setEventoId(evento2.getId());
            item2.setLocalidadId(preferencial.getId());
            item2.setCantidad(3);
            item2.setPrecioUnitario(new BigDecimal("180.00"));

            Compra compra2 = new Compra();
            compra2.setCliente(carlos);
            compra2.setFechaCompra(LocalDateTime.of(2024, 12, 5, 15, 45));
            compra2.setTotal(new BigDecimal("540.00"));
            compra2.setMetodoPago("TARJETA_CREDITO");
            compra2.setItems(List.of(item2));

            mongoTemplate.save(compra2);

            // Generar tiquetes para compra2
            for (int i = 0; i < item2.getCantidad(); i++) {
                Tiquete tiquete = new Tiquete();
                tiquete.setCodigoQR(UUID.randomUUID().toString());
                tiquete.setLocalidadId(preferencial.getId());
                tiquete.setEventoId(evento2.getId());
                mongoTemplate.save(tiquete);
            }

            // Actualizar disponibles de preferencial
            preferencial.setDisponibles(preferencial.getDisponibles() - 3);
            mongoTemplate.save(evento2);

            // Compra 3 - Maria compra 2 tickets Platea para evento3
            ItemCompra item3 = new ItemCompra();
            item3.setEventoId(evento3.getId());
            item3.setLocalidadId(platea.getId());
            item3.setCantidad(2);
            item3.setPrecioUnitario(new BigDecimal("60.00"));

            Compra compra3 = new Compra();
            compra3.setCliente(maria);
            compra3.setFechaCompra(LocalDateTime.of(2025, 1, 10, 18, 20));
            compra3.setTotal(new BigDecimal("120.00"));
            compra3.setMetodoPago("TARJETA_CREDITO");
            compra3.setItems(List.of(item3));

            mongoTemplate.save(compra3);

            // Generar tiquetes para compra3
            for (int i = 0; i < item3.getCantidad(); i++) {
                Tiquete tiquete = new Tiquete();
                tiquete.setCodigoQR(UUID.randomUUID().toString());
                tiquete.setLocalidadId(platea.getId());
                tiquete.setEventoId(evento3.getId());
                mongoTemplate.save(tiquete);
            }

            // Actualizar disponibles de platea
            platea.setDisponibles(platea.getDisponibles() - 2);
            mongoTemplate.save(evento3);

            // Compra 4 - Maria compra 5 tickets Pista para evento4
            ItemCompra item4 = new ItemCompra();
            item4.setEventoId(evento4.getId());
            item4.setLocalidadId(pista.getId());
            item4.setCantidad(5);
            item4.setPrecioUnitario(new BigDecimal("100.00"));

            Compra compra4 = new Compra();
            compra4.setCliente(maria);
            compra4.setFechaCompra(LocalDateTime.of(2025, 2, 1, 20, 0));
            compra4.setTotal(new BigDecimal("500.00"));
            compra4.setMetodoPago("TARJETA_CREDITO");
            compra4.setItems(List.of(item4));

            mongoTemplate.save(compra4);

            // Generar tiquetes para compra4
            for (int i = 0; i < item4.getCantidad(); i++) {
                Tiquete tiquete = new Tiquete();
                tiquete.setCodigoQR(UUID.randomUUID().toString());
                tiquete.setLocalidadId(pista.getId());
                tiquete.setEventoId(evento4.getId());
                mongoTemplate.save(tiquete);
            }

            // Actualizar disponibles de pista
            pista.setDisponibles(pista.getDisponibles() - 5);
            mongoTemplate.save(evento4);

            //VALORACIONES
            Valoracion valoracion1 = new Valoracion();
            valoracion1.setCliente(carlos);
            valoracion1.setEvento(evento1);
            valoracion1.setCalificacion(5);
            valoracion1.setComentario("Increíble concierto, la mejor experiencia");

            Valoracion valoracion2 = new Valoracion();
            valoracion2.setCliente(maria);
            valoracion2.setEvento(evento3);
            valoracion2.setCalificacion(4);
            valoracion2.setComentario("Muy buena obra, actores excelentes");

            mongoTemplate.save(valoracion1);
            mongoTemplate.save(valoracion2);

            //resumen
            System.out.println("✅ Roles creados");
            System.out.println("✅ Estados creados");
            System.out.println("✅ Categorías creadas");
            System.out.println("✅ Usuarios creados");
            System.out.println("✅ Eventos creados con localidades");
            System.out.println("✅ Promociones creadas");
            System.out.println("✅ Compras creadas");
            System.out.println("✅ Tiquetes generados");
            System.out.println("✅ Valoraciones creadas");
            System.out.println("\n🔐 Credenciales:");
            System.out.println("   📧 eduardestf20@gmail.com / eduard123 - ADMIN");
            System.out.println("   📧 angiecarloinatorresnarvaez@gmail.com / angie123 - ADMIN");
            System.out.println("   📧 contacteljohnny@gmail.com / jhonatan123 - ORGANIZADOR");
            System.out.println("   📧 laura.organizadora@gmail.com / laura123 - ORGANIZADOR");
            System.out.println("   📧 carlos.ramirez@gmail.com / carlos123 - CLIENTE");
            System.out.println("   📧 maria.fernandez@gmail.com / maria123 - CLIENTE\n");
        };
    }
}