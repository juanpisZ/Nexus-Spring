package com.nexus.nexus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Table(name = "foro")
@Data
public class Foro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foro")
    private Integer id_foro;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Si tu tabla de MySQL no tiene 'created_at', Hibernate fallará.
    // Si no la necesitas por ahora, puedes comentarla.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "id_creador") // Verifica que en MySQL la columna se llame id_creador
    private Usuario usuario;
}