package com.nexus.nexus.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "rol")
@Data
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol") // Mapeo explícito a la DB
    private Integer idRol;

    @Column(name = "nombre_rol")
    private String nombreRol;
}
