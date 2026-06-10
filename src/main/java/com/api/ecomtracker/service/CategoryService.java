package com.api.ecomtracker.service;

import com.api.ecomtracker.domain.Category;
import com.api.ecomtracker.dto.category.CategoryRequest;
import com.api.ecomtracker.exception.ResourceNotFoundException;
import com.api.ecomtracker.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category register(CategoryRequest request) {
        return categoryRepository.save(new Category(request.getName()));
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    @Transactional(readOnly = true)
    public Page<Category> listActive(Pageable pageable) {
        return categoryRepository.findAllByActiveTrue(pageable);
    }

    @Transactional
    public void inactivate(Long id) {
        Category category = findById(id);
        category.inactivate();
        categoryRepository.save(category);
    }
}
