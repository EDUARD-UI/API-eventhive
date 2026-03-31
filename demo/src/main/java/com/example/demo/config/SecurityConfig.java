package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.security.Users.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilitar @PreAuthorize
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .userDetailsService(customUserDetailsService)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/api-docs",
                            "/api-docs/**",
                            "/v3/api-docs",
                            "/v3/api-docs/**"
                    ).permitAll();

                    // Página principal y recursos estáticos
                    auth.requestMatchers(
                            "/",
                            "/index.html",
                            "/css/**",
                            "/js/**",
                            "/images/**",
                            "/pages/**"
                    ).permitAll();

                    auth.requestMatchers("/api/auth/**").permitAll();
                    auth.requestMatchers("/api/boletos/**").permitAll();

                    // Rutas protegidas por rol
                    auth.requestMatchers("/administracion/**").hasRole("ADMINISTRADOR");
                    auth.requestMatchers("/organizador/**").hasRole("ORGANIZADOR");
                })
                // Configurar autenticación basada en sesión (no JWT)
                .formLogin(form -> form.disable()) // Deshabilitar formulario por defecto
                .httpBasic(httpBasic -> httpBasic.disable()); // Deshabilitar basic auth

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(java.util.Arrays.asList("http://localhost:8082", "http://localhost:3000", "http://127.0.0.1:8082"));
        configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(java.util.Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
