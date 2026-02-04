package com.ElVikingoStore.Viking_App.Services;

import com.ElVikingoStore.Viking_App.DTOs.JwtAuthResponse;
import com.ElVikingoStore.Viking_App.DTOs.LoginUserDto;
import com.ElVikingoStore.Viking_App.DTOs.RegisterDto;

import com.ElVikingoStore.Viking_App.JWT.JwtTokenProvider;

import com.ElVikingoStore.Viking_App.Models.Role;
import com.ElVikingoStore.Viking_App.Models.User;
import com.ElVikingoStore.Viking_App.Models.UserRole;

import com.ElVikingoStore.Viking_App.Repositories.RoleRepo;
import com.ElVikingoStore.Viking_App.Repositories.UserRepo;
import com.ElVikingoStore.Viking_App.Repositories.UserRoleRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Objects;

@Schema(name = "AuthService", description = "Servicio para autenticación y registro de usuarios")
@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRoleRepo userRoleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Operation(summary = "Registro de usuario", description = "Crea un nuevo usuario en el sistema")
    public String registerUser(RegisterDto request) {

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        Role role = validateRole(request.getRoleId());

        User user = new User();
        user.setName(request.getName());
        user.setDni(request.getDni());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setSecondaryPhoneNumber(request.getSecondaryPhoneNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepo.save(user);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        userRoleRepo.save(userRole);

        return "User registered successfully";
    }

    @Operation(summary = "Login de usuario", description = "Inicia sesión en el sistema")
    public JwtAuthResponse loginUser(LoginUserDto loginDto) throws BadCredentialsException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Obtener el usuario autenticado
            String username = authentication.getName();
            UUID userId = userRepo.findIdByEmail(username);

            // Obtener el roleId del usuario
            UUID roleId = null;
            if (userId != null) {
                roleId = userRoleRepo.findByUserId(userId)
                        .map(UserRole::getRoleId) // Asumiendo que UserRole tiene getRoleId o se accede al Role y luego
                                                  // a su ID
                        .orElse(null);
            }

            String jwt = tokenProvider.generateToken(authentication, roleId, userId);
            return new JwtAuthResponse(jwt);
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Operation(summary = "Validar Rol", description = "Valida la existencia de un rol en la base de datos")
    private Role validateRole(UUID roleId) {
        return roleRepo.findById(Objects.requireNonNull(roleId, "El ID del rol no puede ser nulo"))
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + roleId));
    }

    public boolean validateToken(String token) {

        return tokenProvider.validateToken(token);
    }

    @Operation(summary = "Encriptar Contraseña", description = "Encripta la contraseña del usuario")
    private String encodePassword(String password) {

        return passwordEncoder.encode(password);
    }
}
