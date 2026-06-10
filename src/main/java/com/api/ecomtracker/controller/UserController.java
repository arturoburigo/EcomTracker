package com.api.ecomtracker.controller;

import com.api.ecomtracker.domain.User;
import com.api.ecomtracker.dto.user.AdminRegisterRequest;
import com.api.ecomtracker.dto.user.UserRegisterRequest;
import com.api.ecomtracker.dto.user.UserResponse;
import com.api.ecomtracker.service.UserService;
import java.net.URI;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody @Valid UserRegisterRequest request, UriComponentsBuilder uriBuilder) {
        return created(userService.registerCustomer(request), uriBuilder);
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> registerAdmin(
            @RequestBody @Valid AdminRegisterRequest request, UriComponentsBuilder uriBuilder) {
        return created(userService.registerAdmin(request), uriBuilder);
    }

    private ResponseEntity<UserResponse> created(User user, UriComponentsBuilder uriBuilder) {
        URI uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserResponse(user));
    }
}
