package com.api.ecomtracker.service;

import com.api.ecomtracker.domain.Category;
import com.api.ecomtracker.domain.Product;
import com.api.ecomtracker.dto.product.ProductRequest;
import com.api.ecomtracker.exception.ResourceNotFoundException;
import com.api.ecomtracker.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public Product register(ProductRequest request) {
        Category category = categoryService.findById(request.getCategoryId());
        Product product =
                new Product(
                        request.getName(),
                        request.getDescription(),
                        request.getColor(),
                        request.getSize(),
                        request.getPrice(),
                        request.getQuantity(),
                        category);
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Transactional(readOnly = true)
    public Page<Product> listActive(Pageable pageable) {
        return productRepository.findAllByActiveTrue(pageable);
    }

    @Transactional
    public Product updateQuantity(Long id, Integer quantity) {
        Product product = findById(id);
        product.updateQuantity(quantity);
        return productRepository.save(product);
    }

    @Transactional
    public void deactivate(Long id) {
        Product product = findById(id);
        product.deactivate();
        productRepository.save(product);
    }
}
