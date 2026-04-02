package com.example.demo.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.security.Users.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .userDetailsService(customUserDetailsService)
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                )
                .authorizeHttpRequests(auth -> {
                    // Archivos estáticos y home
                    auth.requestMatchers(
                            "/", "/index.html", "/css/**", "/js/**",
                            "/images/**", "/pages/**", "/uploads/**"
                    ).permitAll();

                    // Endpoints de Autenticación
                    auth.requestMatchers("/api/auth/login").permitAll();
                    auth.requestMatchers("/api/auth/logout").permitAll();
                    auth.requestMatchers("/api/auth/registrar-cliente").permitAll();
                    auth.requestMatchers("/api/auth/registrar-organizador").permitAll();

                    // Información pública
                    auth.requestMatchers(HttpMethod.GET, "/api/eventos").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/eventos/{id}").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/categorias").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/categorias/{id}").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/localidades").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/localidades/{id}").permitAll();

                    //(cualquier rol autenticado) datos de session activa 
                    auth.requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated();

                    // Perfil del usuario (cualquier usuario autenticado)
                    auth.requestMatchers(HttpMethod.PUT, "/api/usuarios/perfil").authenticated();
                    auth.requestMatchers(HttpMethod.GET, "/api/usuarios/perfil").authenticated();
                    auth.requestMatchers(HttpMethod.POST, "/api/compras").hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.GET, "/api/compras/historial").hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.GET, "/api/compras/{id}").hasRole("CLIENTE");

                    // Pagos
                    auth.requestMatchers(HttpMethod.POST, "/api/pagos").hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.GET, "/api/pagos/**").hasRole("CLIENTE");

                    // Mis boletos
                    auth.requestMatchers(HttpMethod.GET, "/api/boletos/mis-boletos").hasRole("CLIENTE");

                    // Eventos deseados
                    auth.requestMatchers(HttpMethod.POST, "/api/evento-deseado").hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.GET, "/api/evento-deseado").hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/evento-deseado/{id}").hasRole("CLIENTE");

                    // Valoraciones
                    auth.requestMatchers(HttpMethod.POST, "/api/valoraciones").hasRole("CLIENTE");
                    auth.requestMatchers(HttpMethod.GET, "/api/valoraciones/**").hasRole("CLIENTE");
                    
                    // Compras
                    auth.requestMatchers(HttpMethod.POST, "/api/eventos").hasRole("ORGANIZADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/eventos/{id}").hasRole("ORGANIZADOR");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/eventos/{id}").hasRole("ORGANIZADOR");

                    // Dashboard del organizador
                    auth.requestMatchers(HttpMethod.GET, "/api/eventos/organizador/dashboard").hasRole("ORGANIZADOR");
                    auth.requestMatchers(HttpMethod.GET, "/api/eventos/organizador/mis-eventos").hasRole("ORGANIZADOR");

                    // Promociones
                    auth.requestMatchers(HttpMethod.POST, "/api/promociones").hasRole("ORGANIZADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/promociones/{id}").hasRole("ORGANIZADOR");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/promociones/{id}").hasRole("ORGANIZADOR");

                    // Reportes del organizador
                    auth.requestMatchers(HttpMethod.GET, "/api/reportes/organizador").hasRole("ORGANIZADOR");

                    // Gestión de usuarios
                    auth.requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/usuarios/{id}").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/usuarios/{id}").hasRole("ADMINISTRADOR");

                    // Gestión de roles y estados
                    auth.requestMatchers("/api/roles/**").hasRole("ADMINISTRADOR");
                    auth.requestMatchers("/api/estados/**").hasRole("ADMINISTRADOR");

                    // Reportes generales
                    auth.requestMatchers(HttpMethod.GET, "/api/reportes").hasRole("ADMINISTRADOR");
                    auth.requestMatchers(HttpMethod.GET, "/api/reportes/**").hasRole("ADMINISTRADOR");

                    // Gestión de eventos (admin puede eliminar)
                    auth.requestMatchers(HttpMethod.DELETE, "/api/eventos/**").hasRole("ADMINISTRADOR");

                    //Por defecto, todo lo demás requiere autenticación
                    auth.anyRequest().authenticated();
                })
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:8082",
                "http://localhost:3000",
                "http://127.0.0.1:8082"
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source
                = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
