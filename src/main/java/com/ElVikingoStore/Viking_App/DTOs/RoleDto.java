package com.ElVikingoStore.Viking_App.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Schema(description = "Objeto de transferencia de datos para roles", title = "RoleDto")
@Data
public class RoleDto {
        @Schema(description = "Identificador único del rol", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        private UUID id;
        @Schema(description = "Nombre del rol", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 100)
        private String descripcion;
        @Schema(description = "Permiso del rol", example = "READ", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 100)
        private String permission;
}
