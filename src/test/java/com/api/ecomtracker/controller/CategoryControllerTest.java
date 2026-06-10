package com.api.ecomtracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.ecomtracker.domain.Category;
import com.api.ecomtracker.dto.category.CategoryRequest;
import com.api.ecomtracker.exception.GlobalExceptionHandler;
import com.api.ecomtracker.exception.ResourceNotFoundException;
import com.api.ecomtracker.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryController")
class CategoryControllerTest {

    @Mock private CategoryService categoryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new CategoryController(categoryService))
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();
    }

    @Test
    @DisplayName("POST /categories should return 201 with Location header")
    void registerShouldReturnCreated() throws Exception {
        when(categoryService.register(any(CategoryRequest.class)))
                .thenReturn(new Category(1L, "Shoes", true));

        mockMvc.perform(
                        post("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\": \"Shoes\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/categories/1"))
                .andExpect(jsonPath("$.name").value("Shoes"));
    }

    @Test
    @DisplayName("POST /categories should return 400 when the name is blank")
    void registerShouldReturnBadRequestForBlankName() throws Exception {
        mockMvc.perform(
                        post("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    @DisplayName("GET /categories/{id} should return 404 when the category does not exist")
    void getCategoryShouldReturnNotFound() throws Exception {
        when(categoryService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Category", 99L));

        mockMvc.perform(get("/categories/99")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /categories/{id} should return 204")
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/categories/1")).andExpect(status().isNoContent());
    }
}
