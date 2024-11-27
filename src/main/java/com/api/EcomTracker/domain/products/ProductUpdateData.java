package com.api.EcomTracker.domain.products;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateData {
    @Min(0)
    private Integer quantity;

}