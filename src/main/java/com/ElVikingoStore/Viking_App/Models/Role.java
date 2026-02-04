package com.ElVikingoStore.Viking_App.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Modelo de roles", title = "Role")
@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(of = "id")
public class Role implements Serializable {
	@Schema(description = "Identificador único del rol", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	@Schema(description = "Nombre del rol", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 100)
	@Column(nullable = false, unique = true)
	private String descripcion;

	@Schema(description = "Permiso del rol", example = "READ", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 100)
	@Column(name = "permission", nullable = false)
	private String permission;
	@Schema(description = "Usuarios con este rol", accessMode = Schema.AccessMode.READ_ONLY)
	@OneToMany(mappedBy = "role")
	@JsonIgnore // Evita serializar la relación con UserRole para prevenir ciclos
	private Set<UserRole> userRoles = new HashSet<>();
}
