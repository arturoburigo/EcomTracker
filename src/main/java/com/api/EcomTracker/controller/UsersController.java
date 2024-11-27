package com.api.EcomTracker.controller;

import com.api.EcomTracker.domain.users.dto.UserRegisterDTO;
import com.api.EcomTracker.domain.users.dto.UserRegisterAdminDTO;
import com.api.EcomTracker.domain.users.Users;
import com.api.EcomTracker.domain.users.UsersRepository;
import com.api.EcomTracker.domain.users.dto.UserResponseDTO;
import com.api.EcomTracker.domain.users.dto.UserUpdateDTO;
import com.api.EcomTracker.domain.users.roles.Roles;
import com.api.EcomTracker.domain.users.roles.RolesRepository;
import com.api.EcomTracker.errors.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersRepository repository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO data, UriComponentsBuilder uriBuilder) {
        return registerUser(data, Roles.RoleName.USER, uriBuilder);
    }

    @PostMapping("/register/admin")
    @Transactional
    public ResponseEntity<?> registerWithRole(
            @RequestBody @Valid UserRegisterAdminDTO data,
            Authentication authentication,
            UriComponentsBuilder uriBuilder) {

        // Check if the authenticated user has ADMIN role
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(
                            "What are you doing here buddy?",
                            "This route can only be use by admins with admin token"
                    ));
        }

        // Get the requested role
        Roles role = rolesRepository.findById(data.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Ensure only ADMIN role can be created through this endpoint
        if (!role.getName().equals(Roles.RoleName.ADMIN)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(
                            "Invalid Role",
                            "This endpoint can only be used to create admin users"
                    ));
        }

        return registerUser(data, role.getName(), uriBuilder);
    }

    private ResponseEntity<?> registerUser(UserRegisterDTO data, Roles.RoleName defaultRole, UriComponentsBuilder uriBuilder) {
        try {
            // Validate if user already exists
            if (repository.existsByEmail(data.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(
                                "Registration failed",
                                "User with this email already exists"
                        ));
            }

            // Get the role
            Roles role = rolesRepository.findByName(defaultRole)
                    .orElseThrow(() -> new RuntimeException("Role not found in the database"));

            // Create new user
            Users user = new Users(
                    null,
                    data.getEmail(),
                    passwordEncoder.encode(data.getPassword()),
                    role
            );

            // Save user
            Users savedUser = repository.save(user);

            // Create success response
            URI uri = uriBuilder.path("/users/{id}")
                    .buildAndExpand(savedUser.getId())
                    .toUri();

            return ResponseEntity.created(uri)
                    .body(new UserResponseDTO(savedUser));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(
                            "Registration failed",
                            "An unexpected error occurred while processing your request"
                    ));
        }
    }

    // Adicione estes métodos ao UsersController.java:
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO data,
            Authentication authentication) {

        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(
                            "Access denied",
                            "Only administrators can update user information"
                    ));
        }

        // Verifica se o usuário existe
        Users user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verifica se o email já existe para outro usuário
        if (data.getEmail() != null && !data.getEmail().equals(user.getEmail()) &&
                repository.existsByEmail(data.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(
                            "Update failed",
                            "Email already in use by another user"
                    ));
        }

        try {
            if (data.getEmail() != null) {
                user.setEmail(data.getEmail());
            }

            if (data.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(data.getPassword()));
            }

            repository.save(user);

            return ResponseEntity.ok(new UserResponseDTO(user));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(
                            "Update failed",
                            "An unexpected error occurred while processing your request"
                    ));
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {

        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(
                            "Access denied",
                            "Only administrators can delete users"
                    ));
        }

        try {
            if (!repository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            Users user = repository.findById(id).get();
            if (user.getRole().getName() == Roles.RoleName.ADMIN) {
                long adminCount = repository.findAll().stream()
                        .filter(u -> u.getRole().getName() == Roles.RoleName.ADMIN)
                        .count();

                if (adminCount <= 1) {
                    return ResponseEntity.badRequest()
                            .body(new ErrorResponse(
                                    "Delete failed",
                                    "Cannot delete the last admin user"
                            ));
                }
            }

            repository.deleteById(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(
                            "Delete failed",
                            "An unexpected error occurred while processing your request"
                    ));
        }
    }
    @GetMapping
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(
                            "Access denied",
                            "Only administrators can list users"
                    ));
        }

        try {
            List<Users> users = repository.findAll();
            List<UserResponseDTO> response = users.stream()
                    .map(UserResponseDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(
                            "List failed",
                            "An unexpected error occurred while processing your request"
                    ));
        }
    }
}