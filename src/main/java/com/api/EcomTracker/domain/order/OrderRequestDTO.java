package com.api.EcomTracker.domain.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class OrderRequestDTO {
    @NotNull(message = "Product ID is mandatory")
    private Long productId;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Quantity is mandatory")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;
}