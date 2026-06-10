package com.api.ecomtracker.dto.user;

import com.api.ecomtracker.domain.User;
import lombok.Getter;

@Getter
public class UserResponse {

    private final Long id;

    private final String email;

    private final String username;

    private final String roleName;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.roleName = user.getRole().getName().name();
    }
}
