package com.clinicanuevomilenio.ApiGateway.jwt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // --- CADENA DE FILTROS 1: Para el Frontend (PÚBLICA) ---
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas Públicas y Vistas del Frontend
                        .requestMatchers(
                                "/login/**",
                                "/VistaPrincipalMedico/**",
                                // --- RUTA CORREGIDA ---
                                "/VistaPrincipalJefeDePabellon/**", // Permite acceso a todo dentro de esta carpeta
                                "/gestion-servicios/**",      // <-- Nueva vista de gestión
                                "/VistaJefeServicios/**",
                                "/api/auth/**"
                        ).permitAll()

                        // 2. Reglas de API (Proxy)
                        .requestMatchers("/api/proxy/usuarios/**").hasRole("ADMINISTRATIVO")
                        .requestMatchers("/api/proxy/reservas/pendientes").hasAnyRole("JEFE DE PABELLON", "ADMINISTRATIVO")
                        .requestMatchers(HttpMethod.PUT, "/api/proxy/reservas/**").hasAnyRole("JEFE DE PABELLON", "ADMINISTRATIVO", "MEDICO")
                        .requestMatchers("/api/proxy/reservas/**").hasAnyRole("ADMINISTRATIVO", "MEDICO")
                        .requestMatchers("/api/proxy/solicitudes/**").hasAnyRole("MEDICO", "ADMINISTRATIVO")
                        .requestMatchers("/api/proxy/incidencias/**").hasAnyRole("MEDICO", "ADMINISTRATIVO")
                        .requestMatchers("/api/proxy/pabellones/**").hasAnyRole("MEDICO", "ADMINISTRATIVO")
                        .requestMatchers("/api/proxy/equipamiento/**").hasAnyRole("ADMINISTRATIVO", "MEDICO", "INSTRUMENTISTA")
                        .requestMatchers("/VistaJefeServicios/**", "/gestion-servicios/**").permitAll()
                        .requestMatchers("/api/proxy/solicitudes-servicio/**").hasAnyRole("JEFE_DE_SERVICIOS", "ADMINISTRATIVO")
                        .requestMatchers("/api/proxy/usuarios/por-rol").hasAnyRole("JEFE_DE_SERVICIOS", "ADMINISTRATIVO")

                        // 3. Regla Final
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    // --- Beans de Configuración (sin cambios) ---
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8888"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-User-Id"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}