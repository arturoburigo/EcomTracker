package com.api.EcomTracker.domain.users.dto;

import com.api.EcomTracker.domain.users.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String login;
    private String roleName;

    public UserResponseDTO(Users user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.roleName = user.getRole().getName().toString();
    }
}