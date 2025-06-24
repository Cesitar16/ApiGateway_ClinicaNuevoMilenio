package com.clinicanuevomilenio.ApiGateway.jwt.security;

public class PublicRoutes {

    public static final String[] PUBLIC_GET = {
        "/api/ping",
        "/api/auth/login",
        "/api/auth/login/**"
    };

    public static final String[] PUBLIC_POST = {
        "/api/auth/login",
        "/api/auth/login/**"
    };

}
