package com.api.ecomtracker.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Product")
class ProductTest {

    private Product product() {
        return new Product(
                "Sneaker",
                "Running shoes",
                "Black",
                "42",
                BigDecimal.TEN,
                10,
                new Category("Shoes"));
    }

    @Test
    @DisplayName("new products should start active")
    void newProductShouldStartActive() {
        assertThat(product().getActive()).isTrue();
    }

    @Test
    @DisplayName("hasStockFor should compare the requested quantity with the stock")
    void hasStockForShouldCompareWithStock() {
        Product product = product();

        assertThat(product.hasStockFor(10)).isTrue();
        assertThat(product.hasStockFor(11)).isFalse();
    }

    @Test
    @DisplayName("decreaseStock should subtract from the current quantity")
    void decreaseStockShouldSubtract() {
        Product product = product();

        product.decreaseStock(4);

        assertThat(product.getQuantity()).isEqualTo(6);
    }

    @Test
    @DisplayName("updateQuantity should reject negative and null values")
    void updateQuantityShouldRejectInvalidValues() {
        Product product = product();

        assertThatThrownBy(() -> product.updateQuantity(-1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> product.updateQuantity(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("deactivate should mark the product as inactive")
    void deactivateShouldMarkInactive() {
        Product product = product();

        product.deactivate();

        assertThat(product.getActive()).isFalse();
    }
}
