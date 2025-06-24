package com.clinicanuevomilenio.ApiGateway.redireccion.usuarios;

public class UsuariosPublicRoutes {
    public static final String[] PUBLIC_GET = {
        "/api/proxy/usuarios",
        "/api/proxy/usuarios/",
        "/api/proxy/usuarios/login",
        "/api/proxy/usuarios/login/**"
        // Añade otras rutas GET públicas si existen
    };

    public static final String[] PUBLIC_POST = {
        "/api/proxy/usuarios/login",
        "/api/proxy/usuarios/login/**"
        // Añade otras rutas POST públicas si existen
    };
}
