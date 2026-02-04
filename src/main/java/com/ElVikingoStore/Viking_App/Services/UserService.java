package com.ElVikingoStore.Viking_App.Services;

import java.util.*;

import com.ElVikingoStore.Viking_App.DTOs.User.UserCreateRequestDto;
import com.ElVikingoStore.Viking_App.DTOs.User.UserResponseDto;
import com.ElVikingoStore.Viking_App.DTOs.User.UserUpdateRequestDto;
import com.ElVikingoStore.Viking_App.Exception.UnauthorizedException;
import com.ElVikingoStore.Viking_App.Models.Role;
import com.ElVikingoStore.Viking_App.Models.UserRole;
import com.ElVikingoStore.Viking_App.Repositories.RoleRepo;
import com.ElVikingoStore.Viking_App.Repositories.UserRoleRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ElVikingoStore.Viking_App.Models.User;
import com.ElVikingoStore.Viking_App.Repositories.UserRepo;

import jakarta.transaction.Transactional;

@Schema(name = "UserService", description = "Servicio para la gestión de usuarios")
@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRoleRepo userRoleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void CustomUsersDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Operation(summary = "Obtener todos los usuarios", description = "Obtiene una lista de todos los usuarios registrados en el sistema")
    public List<UserResponseDto> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Operation(summary = "Obtener usuario por ID", description = "Obtiene un usuario por su ID")
    public UserResponseDto getUserById(UUID id) {
        User user = getUserEntityById(id);
        return convertToResponse(user);
    }

    public User getUserEntityById(UUID id) {
        return userRepo.findById(Objects.requireNonNull(id, "ID vacío"))
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Operation(summary = "Obtener usuario por su rolId", description = "Obtiene una lista de usuarios por su rolId")
    public List<UserResponseDto> getUsersByRoleId(UUID roleId) {

        List<UserRole> userRoles = userRoleRepo.findByRole_Id(roleId);

        return userRoles.stream()
                .map(userRole -> convertToResponse(userRole.getUser()))
                .toList();
    }

    @Operation(summary = "Obtener usuario por DNI", description = "Obtiene un usuario por su DNI")
    public User getUserByDni(Integer dni) {
        return userRepo.findByDni(dni)
                .orElseThrow(() -> new NoSuchElementException("User not found with DNI: " + dni));
    }

    @Operation(summary = "Obtener usuario por email", description = "Obtiene un usuario por su email")
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));
    }

    @Operation(summary = "Guardar Instancia de Usuario", description = "Guarda un nuevo usuario en la base de datos")
    @Transactional
    public String saveUserInstance(UserCreateRequestDto userDto) {
        Role role = validateRole(userDto.getRoleId());

        User user = new User();
        user.setName(userDto.getName());
        user.setDni(userDto.getDni());
        user.setAddress(userDto.getAddress());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setSecondaryPhoneNumber(userDto.getSecondaryPhoneNumber());
        user.setEmail(userDto.getEmail());
        user.setPassword(encodePassword(userDto.getPassword()));

        // Guardar el usuario
        userRepo.save(user);

        // Crear y guardar el UserRole
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role); // Establecer el rol recuperado
        userRoleRepo.save(userRole);
        return "User created successfully";
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza un usuario existente en el sistema")
    @Transactional
    public UserResponseDto updateUser(UserUpdateRequestDto request) {

        User user = userRepo.findById(
                Objects.requireNonNull(request.getId(), "ID vacío"))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setDni(request.getDni());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setSecondaryPhoneNumber(request.getSecondaryPhoneNumber());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoleId() != null) {
            Role role = validateRole(request.getRoleId());

            UserRole userRole = userRoleRepo.findByUserId(user.getId())
                    .orElseGet(() -> {
                        UserRole newUserRole = new UserRole();
                        newUserRole.setUser(user);
                        return newUserRole;
                    });

            userRole.setRole(role);
            userRoleRepo.save(userRole);
        }

        userRepo.save(user);

        return convertToResponse(user);
    }

    @Operation(summary = "Encriptar Contraseña", description = "Encripta la contraseña del usuario en caso de necesitarse")
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    public boolean deleteUser(UUID id) {
        try {
            Optional<User> optionalUser = userRepo.findById(Objects.requireNonNull(id, "ID vacio"));
            if (optionalUser.isEmpty()) {
                return false; // Usuario no encontrado
            }

            userRepo.deleteById(id);
            return true; // Usuario eliminado exitosamente
        } catch (Exception e) {
            return false; // Error al eliminar
        }
    }

    @Operation(summary = "Verificar si el usuario tiene un rol específico", description = "Verifica si un usuario tiene un rol específico")
    public boolean hasRoleId(String email, UUID roleId) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Buscar si el usuario tiene el rol especificado
            Optional<UserRole> userRole = userRoleRepo.findByUserId(user.getId());
            return userRole.isPresent() && userRole.get().getRoleId().equals(roleId);
        }
        return false;
    }

    @Operation(summary = "Validar Rol", description = "Valida la existencia de un rol en la base de datos")
    private Role validateRole(UUID roleId) {
        return roleRepo.findById(Objects.requireNonNull(roleId, "ID vacio"))
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + roleId));
    }

    @Operation(summary = "Convertir a DTO", description = "Convierte un objeto User a UserDto")
    private UserResponseDto convertToResponse(User user) {

        UserResponseDto response = new UserResponseDto();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setDni(user.getDni());
        response.setAddress(user.getAddress());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setSecondaryPhoneNumber(user.getSecondaryPhoneNumber());
        response.setEmail(user.getEmail());

        userRoleRepo.findByUserId(user.getId())
                .ifPresent(userRole -> response.setRoleId(userRole.getRole().getId()));

        return response;
    }

    @Operation(summary = "Obtener información del usuario actual", description = "Obtiene la información del usuario autenticado")
    public UserResponseDto getCurrentUserInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UnauthorizedException("No authenticated user");
        }

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return convertToResponse(user);
    }

    @Operation(summary = "Verificacion de Autenticación", description = "Verifica si el usuario está autenticado")
    private Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("No hay usuario autenticado");
        }
        return authentication;
    }

    @Operation(summary = "Obtener el email del usuario autenticado", description = "Obtiene el email del usuario autenticado")
    private String getEmailFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        if (email == null || email.isEmpty()) {
            throw new UnauthorizedException("Email de usuario no válido");
        }
        return email;
    }
}
