package com.api.EcomTracker.domain.categories;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetCategoriesDetail {
    private final Long id;
    private  final String name;

    public GetCategoriesDetail(Categories categories) {
        this(categories.getId(), categories.getName());
    }
}
