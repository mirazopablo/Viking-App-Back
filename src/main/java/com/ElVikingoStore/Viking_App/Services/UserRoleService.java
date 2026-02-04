package com.ElVikingoStore.Viking_App.Services;

import com.ElVikingoStore.Viking_App.Models.Role;
import com.ElVikingoStore.Viking_App.Models.User;
import com.ElVikingoStore.Viking_App.Models.UserRole;
import com.ElVikingoStore.Viking_App.Repositories.UserRepo;
import com.ElVikingoStore.Viking_App.Repositories.UserRoleRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Schema(name = "UserRoleService", description = "Servicio para la gestión de roles de usuario")
@Service
public class UserRoleService {

    @Autowired
    private UserRoleRepo userRoleRepo; // Repositorio de user_roles

    @Autowired
    private UserRepo userRepository; // Repositorio de usuarios

    @Operation(summary = "Obtener rol de usuario", description = "Obtiene el rol de un usuario por su email")
    public String getUserPermission(String email) {
        // Obtener el ID del usuario según el email
        UUID userId = userRepository.findIdByEmail(email); // Método que necesitas agregar en UserRepo

        if (userId != null) {
            // Obtener el rol del usuario
            Optional<UserRole> userRoleOpt = userRoleRepo.findByUserId(userId);
            if (userRoleOpt.isPresent()) {
                // Obtener el permiso correspondiente al rol
                Role role = userRoleOpt.get().getRole();
                return role != null ? role.getPermission() : null;
            }
        }

        return null;
    }

    @Operation(summary = "Verificar si el usuario es staff", description = "Verifica si un usuario es staff por su email")
    public boolean isUserStaff(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return userRoleRepo.findByUser(user)
                .map(userRole -> "staff".equals(userRole.getRole().getDescripcion()))
                .orElse(false);
    }
}
