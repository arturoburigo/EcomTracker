package com.api.EcomTracker.controller;

import com.api.EcomTracker.domain.categories.Categories;
import com.api.EcomTracker.domain.categories.CategoriesRepository;
import com.api.EcomTracker.domain.categories.GetAllCategories;
import com.api.EcomTracker.domain.categories.GetCategoriesDetail;
import com.api.EcomTracker.domain.products.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<GetProductsDetails> register(@RequestBody @Valid ProductsRegisterData data, UriComponentsBuilder uriBuilder) {
        Categories category = categoriesRepository.findById(data.getCategory_id())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Products product = new Products(data, category);
        productsRepository.save(product);

        java.net.URI uri = uriBuilder.path("/products/{id}")
                .buildAndExpand(product.getId())
                .toUri();
        return ResponseEntity.created(uri).body(new GetProductsDetails(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProductsDetails> getProduct(@PathVariable Long id){
        Products products = productsRepository.getReferenceById(id);
        return ResponseEntity.ok(new GetProductsDetails(products));
    }

    @GetMapping
    public ResponseEntity<Page<GetAllProducts>> getAllProducts(
            @PageableDefault(size = 10, sort = {"name"}) Pageable pagination) {

        Page<Products> productsPage = productsRepository.findAllByActiveTrue(pagination);

        Page<GetAllProducts> dtoPage = productsPage.map(product ->
                new GetAllProducts(
                        product.getName(),
                        product.getPrice(),
                        product.getQuantity(),
                        product.getCategory().getName(),
                        product.getActive()
                )
        );

        return ResponseEntity.ok(dtoPage);
    }

}
