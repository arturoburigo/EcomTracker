package com.api.EcomTracker.controller;

import com.api.EcomTracker.domain.order.OrderStatusDTO;
import com.api.EcomTracker.domain.order.Orders;
import com.api.EcomTracker.domain.order.OrdersRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {
    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private OrdersRepository ordersRepository;

    @PostMapping(value = "/stripe", consumes = "application/json")
    public ResponseEntity<?> handleStripeEvent(HttpServletRequest request) {
        try {
            String payload = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            String sigHeader = request.getHeader("Stripe-Signature");
            logger.debug("Received payload: {}", payload);

            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            logger.info("Event type: {}", event.getType());

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
                logger.info("Session metadata: {}", session.getMetadata());
                handleCheckoutSessionCompleted(session);
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Webhook error: {} - {}", e.getClass().getName(), e.getMessage());
            return ResponseEntity.ok().build(); // Always return 200 to Stripe
        }
    }

    private void handleCheckoutSessionCompleted(Session session) {
        try {
            String orderId = session.getMetadata().get("orderId");
            logger.info("Processing checkout completion for order: {}", orderId);

            if (orderId != null) {
                Orders order = ordersRepository.findById(Long.parseLong(orderId))
                        .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
                order.setStatus(OrderStatusDTO.PAID);
                ordersRepository.save(order);
                logger.info("Successfully updated order {} to PAID status", orderId);
            } else {
                logger.warn("No orderId found in session metadata");
            }
        } catch (Exception e) {
            logger.error("Error in handleCheckoutSessionCompleted: {}", e.getMessage(), e);
            throw e;
        }
    }
}