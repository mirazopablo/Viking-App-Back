package com.ElVikingoStore.Viking_App.Resources;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.ElVikingoStore.Viking_App.DTOs.User.UserCreateRequestDto;
import com.ElVikingoStore.Viking_App.DTOs.User.UserResponseDto;
import com.ElVikingoStore.Viking_App.DTOs.User.UserUpdateRequestDto;
import com.ElVikingoStore.Viking_App.Exception.ApiError;
import com.ElVikingoStore.Viking_App.Exception.UnauthorizedException;
import com.ElVikingoStore.Viking_App.Exception.UserNotFoundException;
import com.ElVikingoStore.Viking_App.Services.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.ElVikingoStore.Viking_App.Models.User;
import com.ElVikingoStore.Viking_App.Services.UserService;

@Tag(name = "User Controller", description = "API para la gestión de usuarios")
@RestController
@RequestMapping("/api/user")
public class UserResource {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService; // Servicio para manejar roles de usuario

    @Operation(summary = "Buscar Usuario", description = "Busca un usuario en la base de datos")
    // Búsqueda de usuarios según tipo o parámetros como DNI, CUIT, o ID
    @GetMapping("/search")
    public ResponseEntity<Object> searchUser(@RequestParam(required = false) UUID id,
            @RequestParam(required = false) Integer dni,
            @RequestParam(required = false) UUID roleId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String query) {
        // JWT debería verificar automáticamente el acceso aquí
        try {
            if (query == null) {
                return ResponseEntity.badRequest().body("Query parameter is required");
            }

            switch (query.toLowerCase()) {
                case "all" -> {
                    List<UserResponseDto> users = userService.getAllUsers();
                    return ResponseEntity.ok(users);
                }
                case "by-id" -> {
                    if (id == null) {
                        return ResponseEntity.badRequest().body("ID is required for 'by-id' query");
                    }
                    UserResponseDto userById = userService.getUserById(id);
                    return ResponseEntity.ok(userById);
                }
                case "by-dni" -> {
                    if (dni == null) {
                        return ResponseEntity.badRequest().body("DNI is required for 'by-dni' query");
                    }
                    User userByDni = userService.getUserByDni(dni);
                    return ResponseEntity.ok(userByDni);
                }
                case "by-email" -> {
                    if (email == null) {
                        return ResponseEntity.badRequest().body("Email is required for 'by-email' query");
                    }
                    User userByEmail = userService.getUserByEmail(email);
                    return ResponseEntity.ok(userByEmail);
                }
                case "by-role" -> {
                    if (roleId == null) {
                        return ResponseEntity.badRequest().body("Role ID is required for 'by-roleId' query");
                    }
                    List<UserResponseDto> users = userService.getUsersByRoleId(roleId);
                    return ResponseEntity.ok(users);
                }

                default -> {
                    return ResponseEntity.badRequest().body("Invalid query parameter");
                }
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @Operation(summary = "Guardar Usuario", description = "Guarda un nuevo usuario en la base de datos")
    // Guardar una nueva instancia de usuario (cliente o staff)
    @PostMapping("/save")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserCreateRequestDto userDto) {
        String response = userService.saveUserInstance(userDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar Usuario", description = "Actualiza un usuario existente")
    // Actualizar los detalles de un usuario
    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequestDto userDto) {
        try {
            if (!id.equals(userDto.getId())) {
                return ResponseEntity.badRequest().build();
            }

            UserResponseDto updateUser = userService.updateUser(userDto);
            if (updateUser != null) {
                return ResponseEntity.ok(updateUser); // Respuesta 200 OK con el objeto actualizado
            } else {
                return ResponseEntity.notFound().build(); // Retorna 404 si no se encuentra
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build(); // Retorna 500 en caso de error interno
        }
    }

    @Operation(summary = "Eliminar Usuario", description = "Elimina un usuario existente")
    // Eliminar un usuario por ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        try {
            UserResponseDto existingUser = userService.getUserById(id);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            boolean deleted = userService.deleteUser(existingUser.getId());

            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener Usuario Actual", description = "Obtiene la información del usuario autenticado")
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser() {
        try {
            return ResponseEntity.ok(userService.getCurrentUserInfo());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError("Usuario no encontrado"));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError("No autorizado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Error interno del servidor"));
        }
    }

    @Operation(summary = "Verificar Permisos", description = "Verifica si el usuario autenticado tiene un permiso específico")
    private boolean hasPermission(String requiredPermission) {
        // Obtener el usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Obtener el rol y permiso del usuario desde la base de datos
        String userPermission = userRoleService.getUserPermission(username);

        // Verificar si el usuario tiene el permiso requerido
        return userPermission != null && userPermission.equals(requiredPermission);
    }
}
