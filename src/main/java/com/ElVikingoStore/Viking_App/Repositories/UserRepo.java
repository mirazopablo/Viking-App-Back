package com.ElVikingoStore.Viking_App.Repositories;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ElVikingoStore.Viking_App.Models.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    @Operation(summary = "Buscar usuario por DNI")
    Optional<User> findByDni(Integer dni);

    @Operation(summary = "Buscar usuario por email")
    Optional<User> findByEmail(String email);

    @Operation(summary = "Verifica si existe el usuario por email")
    boolean existsByEmail(String email);

    @Operation(summary = "Buscar ID por email")
    @org.springframework.data.jpa.repository.Query("SELECT u.id FROM User u WHERE u.email = :email")
    UUID findIdByEmail(String email);
}
