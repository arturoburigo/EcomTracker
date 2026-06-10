package com.api.ecomtracker.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckoutResponse {

    private final Long orderId;

    private final String checkoutUrl;
}
