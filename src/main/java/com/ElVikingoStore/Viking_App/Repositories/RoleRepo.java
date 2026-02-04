package com.ElVikingoStore.Viking_App.Repositories;


import com.ElVikingoStore.Viking_App.Models.Role;
import com.ElVikingoStore.Viking_App.Models.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepo extends JpaRepository<Role, UUID> {
    @Operation(summary = "Buscar rol por nombre")
    Optional<Object> findById(Optional<UserRole> roleId);

}
