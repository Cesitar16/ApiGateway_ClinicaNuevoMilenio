package com.clinicanuevomilenio.ApiGateway.jwt.controller;

import com.clinicanuevomilenio.ApiGateway.jwt.dto.AuthResponse;
import com.clinicanuevomilenio.ApiGateway.jwt.dto.LoginRequest;
import com.clinicanuevomilenio.ApiGateway.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
