package com.ElVikingoStore.Viking_App.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;
@Schema(
        description = "Modelo de roles de usuario",
        title = "UserRole"
)
@Data
@Entity
@Table(name = "user_roles")
public class UserRole {
    @Schema(
            description = "Identificador único de la relación usuario-rol",
            example = "123e4567-e89b-12d3-a456-426614174000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Schema(
            description = "Usuario con este rol",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference // Parte inversa de la relación con User
    private User user;
    @Schema(
            description = "Rol del usuario",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonBackReference // Parte inversa de la relación con Role
    private Role role;

    public UUID getRoleId() {
        return role != null ? role.getId() : null; // Manejo de null
    }
}