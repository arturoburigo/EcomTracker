package com.api.ecomtracker.controller;

import com.api.ecomtracker.domain.Category;
import com.api.ecomtracker.dto.category.CategoryRequest;
import com.api.ecomtracker.dto.category.CategoryResponse;
import com.api.ecomtracker.service.CategoryService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> register(
            @RequestBody @Valid CategoryRequest request, UriComponentsBuilder uriBuilder) {
        Category category = categoryService.register(request);
        URI uri = uriBuilder.path("/categories/{id}").buildAndExpand(category.getId()).toUri();
        return ResponseEntity.created(uri).body(new CategoryResponse(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(new CategoryResponse(categoryService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @PageableDefault(sort = "name") Pageable pageable) {
        return ResponseEntity.ok(categoryService.listActive(pageable).map(CategoryResponse::new));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.inactivate(id);
        return ResponseEntity.noContent().build();
    }
}
