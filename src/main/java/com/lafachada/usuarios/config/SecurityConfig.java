package com.lafachada.usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivar CSRF obligatoriamente para APIs REST
            .csrf(csrf -> csrf.disable()) 
            
            // 2. Permitir el paso a las rutas reales de tu UsuarioController
            .authorizeHttpRequests(auth -> auth
                // Ajustado con el prefijo /users que tiene tu @RequestMapping
                .requestMatchers("/users/register", "/users/register/**").permitAll()
                .requestMatchers("/users/login", "/users/login/**").permitAll()
                
                //
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}