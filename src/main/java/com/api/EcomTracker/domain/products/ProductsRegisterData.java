package com.api.EcomTracker.domain.products;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class ProductsRegisterData {
    @NotBlank (message = "Name is mandatory")
    private String name;

    @Nullable
    private String description;

    @Nullable
    private String color;

    @Nullable
    private String size;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer quantity;

    @NotNull
    private Long category_id;
}
