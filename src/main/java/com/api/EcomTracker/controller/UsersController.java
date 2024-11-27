package com.api.EcomTracker.controller;

import com.api.EcomTracker.domain.users.dto.UserRegisterDTO;
import com.api.EcomTracker.domain.users.dto.UserRegisterAdminDTO;
import com.api.EcomTracker.domain.users.Users;
import com.api.EcomTracker.domain.users.UsersRepository;
import com.api.EcomTracker.domain.users.dto.UserResponseDTO;
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

        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(
                            "What are you doing here buddy?",
                            "This route can only be use by admins with admin token"
                    ));
        }

        Roles role = rolesRepository.findById(data.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

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
            if (repository.existsByEmail(data.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(
                                "Registration failed",
                                "User with this email already exists"
                        ));
            }

            Roles role = rolesRepository.findByName(defaultRole)
                    .orElseThrow(() -> new RuntimeException("Role not found in the database"));

            Users user = new Users(
                    null,
                    data.getEmail(),
                    passwordEncoder.encode(data.getPassword()),
                    role
            );

            Users savedUser = repository.save(user);

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
}