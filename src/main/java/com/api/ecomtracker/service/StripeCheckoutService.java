package com.api.ecomtracker.service;

import com.api.ecomtracker.config.StripeProperties;
import com.api.ecomtracker.domain.Order;
import com.api.ecomtracker.exception.BusinessException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class StripeCheckoutService {

    private static final BigDecimal MINOR_UNITS_PER_CURRENCY = BigDecimal.valueOf(100);

    private final StripeProperties stripeProperties;

    public StripeCheckoutService(StripeProperties stripeProperties) {
        this.stripeProperties = stripeProperties;
    }

    public String createCheckoutSession(Order order) {
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(stripeProperties.getSuccessUrl())
                        .setCancelUrl(stripeProperties.getCancelUrl())
                        .putMetadata("orderId", String.valueOf(order.getId()))
                        .addLineItem(buildLineItem(order))
                        .build();
        try {
            return Session.create(params).getUrl();
        } catch (StripeException exception) {
            throw new BusinessException("Could not create the payment session", exception);
        }
    }

    private SessionCreateParams.LineItem buildLineItem(Order order) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(order.getQuantity().longValue())
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(stripeProperties.getCurrency())
                                .setUnitAmount(toMinorUnits(order.getProduct().getPrice()))
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(order.getProduct().getName())
                                                .build())
                                .build())
                .build();
    }

    private long toMinorUnits(BigDecimal price) {
        return price.multiply(MINOR_UNITS_PER_CURRENCY).longValue();
    }
}
