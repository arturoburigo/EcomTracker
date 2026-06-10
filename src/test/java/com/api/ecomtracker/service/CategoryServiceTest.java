package com.api.ecomtracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.api.ecomtracker.domain.Category;
import com.api.ecomtracker.dto.category.CategoryRequest;
import com.api.ecomtracker.exception.ResourceNotFoundException;
import com.api.ecomtracker.repository.CategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService")
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;

    @InjectMocks private CategoryService categoryService;

    @Test
    @DisplayName("register should save an active category with the given name")
    void registerShouldSaveActiveCategory() {
        when(categoryRepository.save(any(Category.class))).thenAnswer(call -> call.getArgument(0));
        CategoryRequest request = new CategoryRequest();
        request.setName("Shoes");

        Category saved = categoryService.register(request);

        assertThat(saved.getName()).isEqualTo("Shoes");
        assertThat(saved.getActive()).isTrue();
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("findById should return the category when it exists")
    void findByIdShouldReturnCategory() {
        Category category = new Category(1L, "Shoes", true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThat(categoryService.findById(1L)).isEqualTo(category);
    }

    @Test
    @DisplayName("findById should throw ResourceNotFoundException when category does not exist")
    void findByIdShouldThrowWhenMissing() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category")
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("inactivate should mark the category as inactive and save it")
    void inactivateShouldMarkCategoryInactive() {
        Category category = new Category(1L, "Shoes", true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(call -> call.getArgument(0));

        categoryService.inactivate(1L);

        assertThat(category.getActive()).isFalse();
        verify(categoryRepository).save(category);
    }
}
