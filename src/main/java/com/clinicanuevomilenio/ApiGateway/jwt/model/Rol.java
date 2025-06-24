package com.clinicanuevomilenio.ApiGateway.jwt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ROL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(nullable = false)
    private String nombre;

    @Column
    private Boolean requiereDisponibilidad;

    @Column
    private String descripcion;
}
