package com.clinicanuevomilenio.ApiGateway.jwt.repository;

import com.clinicanuevomilenio.ApiGateway.jwt.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombre(String nombre);
}
