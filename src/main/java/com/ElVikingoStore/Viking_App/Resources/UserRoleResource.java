package com.ElVikingoStore.Viking_App.Resources;

import com.ElVikingoStore.Viking_App.Services.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Role Controller", description = "API para la gestión de roles de usuario")
@RestController
@RequestMapping("/api/user-roles")
public class UserRoleResource {
    @Autowired
    UserRoleService userRoleService;

    @Operation(summary = "Verificar si el usuario es staff", description = "Verifica si el usuario es staff")
    @GetMapping("/is-staff")
    public ResponseEntity<Boolean> isUserStaff() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Ahora usamos el email como nombre de usuario
        boolean isStaff = userRoleService.isUserStaff(email);
        return ResponseEntity.ok(isStaff);
    }

    @Operation(summary = "Verificar si el usuario es admin", description = "Verifica si el usuario es admin")
    @GetMapping("/user-permission")
    public ResponseEntity<String> getUserPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String permission = userRoleService.getUserPermission(email);
        return ResponseEntity.ok(permission);
    }
}