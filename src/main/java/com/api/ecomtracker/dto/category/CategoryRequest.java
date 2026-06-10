package com.api.ecomtracker.dto.category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Name is mandatory")
    @Size(max = 30, message = "Name can't be more than 30 letters")
    private String name;
}
