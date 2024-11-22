package com.api.EcomTracker.domain.order;

import com.api.EcomTracker.domain.products.Products;
import com.api.EcomTracker.domain.products.ProductsRepository;
import com.api.EcomTracker.domain.users.Users;
import com.api.EcomTracker.errors.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import java.math.BigDecimal;

@Service
public class OrderService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Transactional
    public ResponseEntity<?> createOrder(OrderRequestDTO orderRequest) {
        try {
            // Obter o usuário autenticado
            Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Validar o produto
            Products product = productsRepository.findById(orderRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Verificar a quantidade em estoque
            if (product.getQuantity() < orderRequest.getQuantity()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Insufficient stock",
                                String.format("Available quantity: %d", product.getQuantity())));
            }

            // Calcular o preço total
            BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(orderRequest.getQuantity()));

            // Atualizar o estoque do produto
            int newQuantity = product.getQuantity() - orderRequest.getQuantity();
            product.updateQuantity(newQuantity);
            productsRepository.save(product);

            // Criar e salvar o pedido com status PENDING
            Orders order = new Orders(
                    product,
                    user,
                    totalPrice,
                    orderRequest.getQuantity(),
                    OrderStatusDTO.PENDING
            );

            Orders savedOrder = ordersRepository.save(order);

            // Criar os parâmetros da sessão do Stripe
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://arturoburigo.shop/success")
                    .setCancelUrl("https://arturoburigo.shop/cancel")
                    .putMetadata("orderId", String.valueOf(savedOrder.getId()))
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity((long) orderRequest.getQuantity())
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(product.getPrice()
                                                            .multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(product.getName())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            // Criar a sessão de pagamento do Stripe
            Session session = Session.create(params);

            // Retornar a URL de checkout para o cliente
            return ResponseEntity.ok(session.getUrl());

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Order creation failed", e.getMessage()));
        }
    }
}