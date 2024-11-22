package com.api.EcomTracker.domain.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Setter
@Getter
@NoArgsConstructor
public class OrderRequestDTO {
    @NotNull(message = "Product ID is mandatory")
    private Long productId;

    @NotNull(message = "Quantity is mandatory")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;
}