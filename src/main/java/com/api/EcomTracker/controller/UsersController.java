package com.api.EcomTracker.controller;

import com.api.EcomTracker.domain.users.dto.UserRegisterDTO;
import com.api.EcomTracker.domain.users.Users;
import com.api.EcomTracker.domain.users.UsersRepository;
import com.api.EcomTracker.domain.users.dto.UserResponseDTO;
import com.api.EcomTracker.domain.users.roles.Roles;
import com.api.EcomTracker.domain.users.roles.RolesRepository;
import com.api.EcomTracker.errors.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        try {
            // Validate if user already exists
            if (repository.existsByLogin(data.getLogin())) {
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        "Registration failed",
                        "User with this email already exists"
                ));
            }

            // Get the USER role
            Roles userRole = rolesRepository.findByName(Roles.RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("Default USER role not found in the database"));

            // Create new user
            Users user = new Users(
                    null,
                    data.getLogin(),
                    passwordEncoder.encode(data.getPassword()),
                    userRole
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
}