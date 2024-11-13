package com.api.EcomTracker.domain.users.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterAdminDTO extends UserRegisterDTO {
    @NotNull(message = "Role ID is mandatory")
    private Long roleId;
}