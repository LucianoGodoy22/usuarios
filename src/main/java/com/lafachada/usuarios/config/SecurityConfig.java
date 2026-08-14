package com.lafachada.usuarios.config;

import org.springframework.context.annotation.Bean; // Importar
import org.springframework.context.annotation.Configuration; // Importar
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.lafachada.usuarios.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor; // Importar

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // Añadir Lombok para inyectar dependencias
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; // Inyectar el filtro

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/login", "/users/register").permitAll()
                .requestMatchers("/users/login", "/users/register", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); 
            
        return http.build();
    }
}