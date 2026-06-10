package com.api.ecomtracker.controller;

import com.api.ecomtracker.config.StripeProperties;
import com.api.ecomtracker.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    private static final String CHECKOUT_COMPLETED_EVENT = "checkout.session.completed";

    private static final String ORDER_ID_METADATA_KEY = "orderId";

    private final StripeProperties stripeProperties;

    private final OrderService orderService;

    public StripeWebhookController(StripeProperties stripeProperties, OrderService orderService) {
        this.stripeProperties = stripeProperties;
        this.orderService = orderService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<Void> handleStripeEvent(
            @RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, stripeProperties.getWebhookSecret());
        } catch (SignatureVerificationException exception) {
            logger.warn("Rejected webhook with invalid signature", exception);
            return ResponseEntity.badRequest().build();
        }

        if (CHECKOUT_COMPLETED_EVENT.equals(event.getType())) {
            handleCheckoutCompleted(event);
        }
        return ResponseEntity.ok().build();
    }

    private void handleCheckoutCompleted(Event event) {
        Session session =
                (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) {
            logger.warn("Could not deserialize checkout session from event {}", event.getId());
            return;
        }

        String orderId = session.getMetadata().get(ORDER_ID_METADATA_KEY);
        if (orderId == null) {
            logger.warn("No orderId found in session metadata for event {}", event.getId());
            return;
        }

        orderService.markAsPaid(Long.parseLong(orderId));
        logger.info("Order {} marked as paid", orderId);
    }
}
