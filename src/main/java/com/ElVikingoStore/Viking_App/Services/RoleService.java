package com.ElVikingoStore.Viking_App.Services;

import com.ElVikingoStore.Viking_App.DTOs.RoleDto;
import com.ElVikingoStore.Viking_App.Models.Role;
import com.ElVikingoStore.Viking_App.Repositories.RoleRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Schema(name = "RoleService", description = "Servicio para la gestión de roles")
@Service
public class RoleService {
    @Autowired
    private RoleRepo roleRepo;

    @Operation(summary = "Obtener todos los roles", description = "Obtiene una lista de todos los roles registrados en el sistema")
    public List<RoleDto> getAllRoles() {
        return roleRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener rol por ID", description = "Obtiene un rol por su ID")
    public Optional<RoleDto> getRoleById(UUID id) {
        return roleRepo.findById(Objects.requireNonNull(id, "ID vacio")).map(this::convertToDTO);
    }

    @Operation(summary = "Crear rol", description = "Crea un nuevo rol en el sistema")
    public RoleDto createRole(RoleDto roleDto) {
        Role role = convertToEntity(roleDto);
        Role savedRole = roleRepo.save(Objects.requireNonNull(role, "Role vacio"));
        return convertToDTO(savedRole);
    }

    @Operation(summary = "Actualizar rol", description = "Actualiza un rol existente en el sistema")
    public Optional<RoleDto> updateRole(UUID id, RoleDto roleDto) {
        return roleRepo.findById(Objects.requireNonNull(id, "ID vacio"))
                .map(role -> {
                    role.setDescripcion(roleDto.getDescripcion());
                    role.setPermission(roleDto.getPermission());
                    return convertToDTO(roleRepo.save(role));
                });
    }

    @Operation(summary = "Eliminar rol", description = "Elimina un rol del sistema")
    public void deleteRole(UUID id) {
        roleRepo.deleteById(Objects.requireNonNull(id, "ID vacio"));
    }

    @Operation(summary = "Convertir a DTO", description = "Convierte un objeto Role a RoleDto")
    private RoleDto convertToDTO(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setDescripcion(role.getDescripcion());
        dto.setPermission(role.getPermission());
        return dto;
    }

    @Operation(summary = "Convertir a entidad", description = "Convierte un objeto RoleDto a Role")
    private Role convertToEntity(RoleDto dto) {
        Role role = new Role();
        role.setDescripcion(dto.getDescripcion());
        role.setPermission(dto.getPermission());
        return role;
    }
}
