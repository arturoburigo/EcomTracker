package com.api.ecomtracker.controller;

import com.api.ecomtracker.domain.Product;
import com.api.ecomtracker.dto.product.ProductRequest;
import com.api.ecomtracker.dto.product.ProductResponse;
import com.api.ecomtracker.dto.product.ProductSummaryResponse;
import com.api.ecomtracker.dto.product.ProductUpdateRequest;
import com.api.ecomtracker.service.ProductService;
import java.net.URI;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> register(
            @RequestBody @Valid ProductRequest request, UriComponentsBuilder uriBuilder) {
        Product product = productService.register(request);
        URI uri = uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(uri).body(new ProductResponse(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(new ProductResponse(productService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<Page<ProductSummaryResponse>> getAllProducts(
            @PageableDefault(sort = "name") Pageable pageable) {
        return ResponseEntity.ok(productService.listActive(pageable).map(ProductSummaryResponse::new));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProductQuantity(
            @PathVariable Long id, @RequestBody @Valid ProductUpdateRequest request) {
        Product product = productService.updateQuantity(id, request.getQuantity());
        return ResponseEntity.ok(new ProductResponse(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
