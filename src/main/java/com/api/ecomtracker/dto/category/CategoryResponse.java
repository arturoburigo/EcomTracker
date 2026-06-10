package com.api.ecomtracker.dto.category;

import com.api.ecomtracker.domain.Category;
import lombok.Getter;

@Getter
public class CategoryResponse {

    private final Long id;

    private final String name;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
