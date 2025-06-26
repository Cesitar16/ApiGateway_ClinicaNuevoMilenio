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

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpMethod;

import java.util.Arrays; // <-- Importación necesaria

// Importamos las constantes estáticas de tu clase
import static com.clinicanuevomilenio.ApiGateway.jwt.security.PublicRoutes.*;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // --- CADENA DE FILTROS 1: Para el Frontend (PÚBLICA) ---
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
<<<<<<< LucasMoncadaCLDefinitivo
=======
                // Se aplica solo a las rutas del frontend
                .securityMatcher(
                        "/login/**",
                        "/VistaPrincipalMedico/**",
                        "/solicitud-servicio/**",
                        "/mis-reservas/**",
                        "/reportar-incidencia/**", // <-- AÑADIDO AQUÍ
                        "/css/**",
                        "/js/**",
                        "/favicon.ico"
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    // Permite el acceso a todas estas rutas sin autenticación.
                    auth.anyRequest().permitAll();
                })
                .build();
    }

    // --- CADENA DE FILTROS 2: Para la API (SEGURA) ---
    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        return http

                // Se aplica a todas las rutas de la API
                .securityMatcher("/api/**")

>>>>>>> main
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
<<<<<<< LucasMoncadaCLDefinitivo
                        // 1. Rutas Públicas y Vistas del Frontend
                        .requestMatchers(
                                "/login/**",
                                "/VistaPrincipalMedico/**",
                                // --- RUTA CORREGIDA ---
                                "/VistaPrincipalJefeDePabellon/**", // Permite acceso a todo dentro de esta carpeta
                                "/api/auth/**"
                        ).permitAll()
=======

                        // Rutas públicas de la API (login/registro)
                        .requestMatchers("/api/auth/**").permitAll()
>>>>>>> main

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

<<<<<<< LucasMoncadaCLDefinitivo
                        // 3. Regla Final
=======
                        // Cualquier otra ruta de la API requiere autenticación
                        // 1. Rutas Públicas de la API
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET).permitAll()
                        .requestMatchers(HttpMethod.POST, PUBLIC_POST).permitAll()
                        // 2. Permitir acceso a todos los archivos del Front-End
                        .requestMatchers("/login/**", "/VistaPrincipalMedico/**").permitAll() // Se añade VistaPrincipalMedico
                        // 3. Reglas específicas para roles
                        .requestMatchers("/api/proxy/usuarios/**").hasRole("ADMINISTRATIVO")
                        .requestMatchers("/api/proxy/reservas/").hasRole("ADMINISTRATIVO")
                        .requestMatchers("/api/proxy/reservas/**").hasAnyRole("ADMINISTRATIVO", "MEDICO")
                        .requestMatchers("/api/proxy/imagenes/**").hasAnyRole("MEDICO")
                        .requestMatchers("/api/proxy/pabellones/**").authenticated()
                        .requestMatchers("/api/proxy/pabellones/filtro").hasAnyRole("MEDICO")
                        .requestMatchers("/api/proxy/pabellones/estado").hasAnyRole("MEDICO")
                        .requestMatchers("/api/proxy/equipamiento/**").hasAnyRole("ADMINISTRATIVO", "INSTUMENTISTA")
                        .requestMatchers("/apu/proxy/equipamiento//stock/pabellon/{pabellonId}/equipo/{equipamientoId}").hasAnyRole("JEFE DE PABELLON", "MEDICO")
                        // 4. Cualquier otra petición requiere autenticación
>>>>>>> main
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
        configuration.setAllowedOrigins(Arrays.asList("*")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
