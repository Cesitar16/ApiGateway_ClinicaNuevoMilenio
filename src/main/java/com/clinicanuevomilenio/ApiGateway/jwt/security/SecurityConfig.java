package com.clinicanuevomilenio.ApiGateway.jwt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpMethod;

// Importamos las constantes estáticas de tu clase
import static com.clinicanuevomilenio.ApiGateway.jwt.security.PublicRoutes.*; //

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter; //

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) //
                .formLogin(form -> form.disable()) //
                .httpBasic(basic -> basic.disable()) //
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //

                .authorizeHttpRequests(auth -> auth
                        // 1. RUTAS PÚBLICAS: No requieren token.
                        .requestMatchers("/api/auth/login").permitAll() //
                        .requestMatchers("/api/ping").permitAll() //

                        // 2. RUTAS POR ROL ESPECÍFICO
                        // Solo un ADMINISTRATIVO puede gestionar usuarios.
                        .requestMatchers("/api/proxy/usuarios/**").hasRole("ADMINISTRATIVO") //

                        // Solo un ADMINISTRATIVO puede asignar una solicitud de servicio.
                        .requestMatchers(HttpMethod.POST, "/api/proxy/solicitudes/asignar").hasRole("ADMINISTRATIVO") //

                        // Para gestionar reservas, se necesita ser ADMINISTRATIVO o MEDICO.
                        .requestMatchers("/api/proxy/reservas/**").hasAnyRole("ADMINISTRATIVO", "MEDICO") //

                        // Para gestionar el equipamiento general, se necesita ser ADMINISTRATIVO o INSTUMENTISTA.
                        .requestMatchers("/api/proxy/equipamiento/**").hasAnyRole("ADMINISTRATIVO", "INSTUMENTISTA") //

                        // 3. RUTAS PARA CUALQUIER USUARIO AUTENTICADO
                        // Cualquier usuario logueado puede ver pabellones, imágenes y crear/ver solicitudes.
                        .requestMatchers("/api/proxy/pabellones/**").authenticated() //
                        .requestMatchers("/api/proxy/imagenes/**").authenticated() //
                        .requestMatchers("/api/proxy/solicitudes/**").authenticated() // Regla general para solicitudes

                        // 4. CUALQUIER OTRA RUTA no definida explícitamente requiere autenticación.
                        .anyRequest().authenticated() //
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) //
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