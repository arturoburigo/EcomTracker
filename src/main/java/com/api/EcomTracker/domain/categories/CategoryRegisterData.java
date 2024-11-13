package com.api.EcomTracker.domain.categories;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
public class CategoryRegisterData {
    @NotBlank(message = "Name is mandatory")
    @Size(max = 30, message = "Name can't be more than 30 letters")
    private String name;

}
