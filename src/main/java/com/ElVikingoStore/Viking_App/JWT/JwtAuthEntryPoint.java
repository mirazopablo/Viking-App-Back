package com.ElVikingoStore.Viking_App.JWT;

import java.io.IOException;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Operation(summary = "Manejo de autenticación JWT",
            description = "Devuelve un error 401 si el usuario no está autenticado.")
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Acceso denegado. Debe estar autenticado en el sistema.");
    }
}