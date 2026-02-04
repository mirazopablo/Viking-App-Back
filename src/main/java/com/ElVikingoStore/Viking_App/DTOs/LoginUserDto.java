package com.ElVikingoStore.Viking_App.DTOs;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "LoginUserDto", description = "DTO para el inicio de sesión")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDto {
    @Schema(description = "Correo electrónico del usuario", example = "example@example.com")
    private String email;
    @Schema(description = "Contraseña del usuario", example = "password")
    private String password;

}
