package com.clinicanuevomilenio.ApiGateway.jwt.service;

import com.clinicanuevomilenio.ApiGateway.jwt.dto.AuthResponse;
import com.clinicanuevomilenio.ApiGateway.jwt.dto.LoginRequest;
import com.clinicanuevomilenio.ApiGateway.jwt.model.Usuario;
import com.clinicanuevomilenio.ApiGateway.jwt.repository.UsuarioRepository;
import com.clinicanuevomilenio.ApiGateway.jwt.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        // Autenticar con username y password
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getNombreUsuario(), request.getPassword()));

        // Buscar usuario en BD
        Usuario usuario = usuarioRepository.findByNombreUsuario(request.getNombreUsuario())
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        // Convertir Boolean estado a String
        String estadoStr = Boolean.TRUE.equals(usuario.getEstado()) ? "activo" : "inactivo";

        // Verificar estado activo
        if (!"activo".equalsIgnoreCase(estadoStr)) {
            throw new BadCredentialsException("Usuario inactivo");
        }

        // Generar token con nombreUsuario, rol y estado en String
        String token = jwtUtil.generateToken(usuario.getNombreUsuario(), usuario.getRol().getNombre(), estadoStr);

        // Retornar respuesta con token, username, rol y estado como String
        return new AuthResponse(token, usuario.getNombreUsuario(), usuario.getRol().getNombre(), estadoStr, usuario.getIdUsuario());
    }

}
