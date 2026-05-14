package com.nexus.nexus.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;
import jakarta.validation.constraints.*;

@Data
@Entity
@Table(name = "usuarios")
@NoArgsConstructor
@AllArgsConstructor
@Builder // Útil para crear usuarios de prueba rápidamente
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombres;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo no válido")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    // Eliminamos el @Size aquí porque el hash de BCrypt siempre es largo (60 caracteres)
    // y la validación de "mínimo 6" debe hacerse en el DTO o Formulario, no en la Entidad
    @Column(name = "password_hash")
    private String password;

    @Transient
    private String confirmPassword;

    // FetchType.EAGER es vital para que el Login no dé error de sesión cerrada
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "estado")
    private String estado = "ACTIVO";

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Se asegura que al persistir se guarde la fecha actual si es nula
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
    }

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Evita bucles infinitos en el log de Lombok
    private List<Evento> eventosCreados;

    public int getCantidadEventos() {
        return (eventosCreados != null) ? eventosCreados.size() : 0;
    }
}