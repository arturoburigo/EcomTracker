package com.api.EcomTracker.domain.users.dto;

import com.api.EcomTracker.domain.users.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String email;
    private String roleName;
    private String username;

    public UserResponseDTO(Users user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.roleName = user.getRole().getName().toString();
    }
}