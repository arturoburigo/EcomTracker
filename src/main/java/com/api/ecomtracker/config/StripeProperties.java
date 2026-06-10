package com.api.ecomtracker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "stripe")
@Getter
@Setter
public class StripeProperties {

    private String apiKey;

    private String webhookSecret;

    private String currency;

    private String successUrl;

    private String cancelUrl;
}
