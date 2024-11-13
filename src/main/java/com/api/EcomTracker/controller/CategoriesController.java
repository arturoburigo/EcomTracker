package com.api.EcomTracker.controller;

import com.api.EcomTracker.domain.categories.*;
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
@RequestMapping("/categories")
public class CategoriesController {
    @Autowired
    private CategoriesRepository categoriesRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<GetCategoriesDetail> register(@RequestBody @Valid CategoryRegisterData data, UriComponentsBuilder uriBuilder) {
        Categories categories = new Categories(data);
        categoriesRepository.save(categories);

        java.net.URI uri = uriBuilder.path("/categories/{id}")
                .buildAndExpand(categories.getId())
                .toUri();
        return ResponseEntity.created(uri).body(new GetCategoriesDetail(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetCategoriesDetail> getCategory(@PathVariable Long id){
        Categories category = categoriesRepository.getReferenceById(id);
        return ResponseEntity.ok(new GetCategoriesDetail(category));
    }

    @GetMapping
    public  ResponseEntity<Page<GetAllCategories>> getAllCategories (@PageableDefault(size = 10, sort = {"name"})Pageable pagination){
        Page<GetAllCategories> categoriesPage = categoriesRepository.findAllByActiveTrue(pagination).map(GetAllCategories::new);
        return ResponseEntity.ok(categoriesPage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GetCategoriesDetail> deleteCategory (@PathVariable Long id) {
        Categories category = categoriesRepository.getReferenceById(id);
        category.inactivate();
        categoriesRepository.save(category);

        return ResponseEntity.noContent().build();
    }
}
