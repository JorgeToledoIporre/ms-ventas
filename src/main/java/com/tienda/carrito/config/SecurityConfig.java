package com.tienda.carrito.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para APIs REST
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/carrito/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/actuator/**"
                ).permitAll() // Permitir acceso sin autenticación (temporal)
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}