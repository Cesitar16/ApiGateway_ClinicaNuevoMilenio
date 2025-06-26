package com.clinicanuevomilenio.ApiGateway.jwt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpMethod;

import java.util.Arrays; // <-- Importación necesaria

// Importamos las constantes estáticas de tu clase
import static com.clinicanuevomilenio.ApiGateway.jwt.security.PublicRoutes.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
