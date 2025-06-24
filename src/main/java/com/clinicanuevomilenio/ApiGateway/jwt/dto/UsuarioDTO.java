package com.clinicanuevomilenio.ApiGateway.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDTO {
    private String nombreUsuario;
    private String rol;
    private String estado;
}
