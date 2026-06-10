package com.api.ecomtracker.config;

import com.stripe.Stripe;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    private final StripeProperties stripeProperties;

    public StripeConfig(StripeProperties stripeProperties) {
        this.stripeProperties = stripeProperties;
    }

    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = stripeProperties.getApiKey();
    }
}
