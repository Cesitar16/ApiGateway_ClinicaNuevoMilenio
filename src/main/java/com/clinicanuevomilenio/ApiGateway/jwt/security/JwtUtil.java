package com.clinicanuevomilenio.ApiGateway.jwt.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // ✅ Asegúrate de que esta clave tenga al menos 64 bytes para HS512
    private final String SECRET = "clave_super_segura_de_mas_de_64_bytes_para_seguridad_HS512_ClinicaMilenio";

    private final long EXPIRATION_TIME = 86400000; // 24 horas en milisegundos

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token con nombre de usuario, rol y estado.
     */
    public String generateToken(String nombreUsuario, String rol, String estado) {
        return Jwts.builder()
                .setSubject(nombreUsuario)
                .claim("username", nombreUsuario)
                .claim("rol", rol)        // se incluye el nombre del rol
                .claim("estado", estado)  // se incluye el estado ("activo", etc.)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrae el username (subject) del token.
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrae un claim específico del token.
     */
    public <T> T extractClaim(String token, String claimName, Class<T> clazz) {
        return getClaims(token).get(claimName, clazz);
    }

    /**
     * Verifica si el token es válido (firma y expiración).
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
