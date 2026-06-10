package com.api.ecomtracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.api.ecomtracker.domain.Category;
import com.api.ecomtracker.domain.Product;
import com.api.ecomtracker.dto.product.ProductRequest;
import com.api.ecomtracker.exception.ResourceNotFoundException;
import com.api.ecomtracker.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService")
class ProductServiceTest {

    @Mock private ProductRepository productRepository;

    @Mock private CategoryService categoryService;

    @InjectMocks private ProductService productService;

    private Product activeProduct(Long id, int quantity) {
        Category category = new Category(1L, "Shoes", true);
        return new Product(
                id,
                "Sneaker",
                "Running shoes",
                "Black",
                "42",
                BigDecimal.TEN,
                quantity,
                true,
                category);
    }

    @Test
    @DisplayName("register should resolve the category and save an active product")
    void registerShouldSaveActiveProduct() {
        Category category = new Category(1L, "Shoes", true);
        when(categoryService.findById(1L)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenAnswer(call -> call.getArgument(0));

        ProductRequest request = new ProductRequest();
        request.setName("Sneaker");
        request.setPrice(BigDecimal.TEN);
        request.setQuantity(5);
        request.setCategoryId(1L);

        Product saved = productService.register(request);

        assertThat(saved.getName()).isEqualTo("Sneaker");
        assertThat(saved.getCategory()).isEqualTo(category);
        assertThat(saved.getActive()).isTrue();
    }

    @Test
    @DisplayName("findById should throw ResourceNotFoundException when product does not exist")
    void findByIdShouldThrowWhenMissing() {
        when(productRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product");
    }

    @Test
    @DisplayName("updateQuantity should change the stock when the quantity is valid")
    void updateQuantityShouldChangeStock() {
        Product product = activeProduct(1L, 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(call -> call.getArgument(0));

        Product updated = productService.updateQuantity(1L, 8);

        assertThat(updated.getQuantity()).isEqualTo(8);
    }

    @Test
    @DisplayName("updateQuantity should reject negative quantities")
    void updateQuantityShouldRejectNegativeValues() {
        Product product = activeProduct(1L, 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateQuantity(1L, -1))
                .isInstanceOf(IllegalArgumentException.class);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("deactivate should mark the product as inactive and save it")
    void deactivateShouldMarkProductInactive() {
        Product product = activeProduct(1L, 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(call -> call.getArgument(0));

        productService.deactivate(1L);

        assertThat(product.getActive()).isFalse();
        verify(productRepository).save(product);
    }
}
