package com.clinicanuevomilenio.ApiGateway.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String nombreUsuario;
    private String rol;  // Nombre del rol
    private String estado; // Ej: "activo", "inactivo"
}
