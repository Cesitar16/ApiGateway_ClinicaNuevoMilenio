package com.clinicanuevomilenio.ApiGateway.jwt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpMethod;

// Importamos las constantes estáticas de tu clase
import static com.clinicanuevomilenio.ApiGateway.jwt.security.PublicRoutes.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas Públicas (sin cambios)
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET).permitAll()
                        .requestMatchers(HttpMethod.POST, PUBLIC_POST).permitAll()

                        // 2. Regla para Administrar Usuarios (sin cambios)
                        .requestMatchers("/api/proxy/usuarios/**").hasRole("ADMINISTRATIVO")

                        // 3. --- NUEVA REGLA PARA RESERVAS ---
                        // Permitimos a Cirujanos y Administrativos gestionar reservas.
                        // Usamos hasAnyRole para permitir múltiples roles.
                        .requestMatchers("/api/proxy/reservas/**").hasAnyRole("ADMINISTRATIVO", "MEDICO")

                        // 4. --- NUEVA REGLA PARA PABELLONES ---
                        // Cualquier usuario autenticado puede ver/interactuar con los pabellones.
                        .requestMatchers("/api/proxy/pabellones/**").authenticated()

                        // 5. Cualquier otra petición no definida anteriormente requiere autenticación.
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
