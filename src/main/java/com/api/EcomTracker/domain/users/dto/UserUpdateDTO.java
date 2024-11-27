package com.api.EcomTracker.domain.users.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class UserUpdateDTO {
    @Email
    private String email;

    @Size(min = 6)
    private String password;

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}