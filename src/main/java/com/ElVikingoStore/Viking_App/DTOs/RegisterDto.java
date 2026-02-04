package com.ElVikingoStore.Viking_App.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class RegisterDto {

    @Schema(description = "Nombre completo del usuario", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Documento Nacional de Identidad", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer dni;

    @Schema(description = "Dirección del usuario", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    @Schema(description = "Número de teléfono principal", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;

    private String secondaryPhoneNumber;

    @Schema(description = "Correo electrónico del usuario", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Contraseña del usuario", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "Identificador del rol", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID roleId;
}
