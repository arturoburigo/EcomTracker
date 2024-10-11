package com.api.EcomTracker.domain.products;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class GetAllProducts {
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String categoryName;
    private  Boolean active;
}
