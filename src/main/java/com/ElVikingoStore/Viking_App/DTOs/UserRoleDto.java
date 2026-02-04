package com.ElVikingoStore.Viking_App.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Schema(
        description = "Objeto de transferencia de datos para roles de usuario",
        title = "UserRoleDto"
)
@Data
public class UserRoleDto {
    @Schema(
            description = "Identificador único de la relación usuario-rol",
            example = "123e4567-e89b-12d3-a456-426614174000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private UUID id;
    @Schema(
            description = "Identificador único del usuario",
            example = "123e4567-e89b-12d3-a456-426614174000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private UUID userId; // O el objeto User si es necesario
    @Schema(
            description = "Identificador único del rol",
            example = "123e4567-e89b-12d3-a456-426614174000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private UUID roleId; // O el objeto Rol si es necesario
}
