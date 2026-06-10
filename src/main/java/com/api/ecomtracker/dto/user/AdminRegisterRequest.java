package com.api.ecomtracker.dto.user;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminRegisterRequest extends UserRegisterRequest {

    @NotNull(message = "Role ID is mandatory")
    private Long roleId;
}
