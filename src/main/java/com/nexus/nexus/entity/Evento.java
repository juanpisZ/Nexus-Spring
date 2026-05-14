package com.nexus.nexus.entity;

import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "evento")
@Data
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_evento;

    @NotNull
    private String titulo;

    private String descripcion;
    private String categoria;
    private String ubicacion;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "La fecha de inicio no puede ser en el pasado")
    @Column(name = "f_inicio")
    private LocalDate f_inicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "La fecha de fin no puede ser en el pasado")
    @Column(name = "f_fin")
    private LocalDate f_fin;

    @Min(value = 1, message = "El límite de participantes debe ser al menos 1")
    @Column(name = "limite_participantes")
    private Integer limite_participantes;

    @ManyToOne
    @JoinColumn(name = "id_creador")
    private Usuario usuario;
}