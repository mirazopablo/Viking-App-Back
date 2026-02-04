package com.ElVikingoStore.Viking_App.Repositories;

import com.ElVikingoStore.Viking_App.Models.User;
import com.ElVikingoStore.Viking_App.Models.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole, UUID> {
    @Operation(summary = "Buscar roles por ID de usuario")
    List<UserRole> findByRole_Id(UUID roleId); // Acceso correcto al ID del rol

    @Operation(summary = "Buscar UserRole por ID de usuario")
    Optional<UserRole> findByUserId(UUID userId);

    @Operation(summary = "Buscar roles por usuario")
    Optional<UserRole> findByUser(User user);
}