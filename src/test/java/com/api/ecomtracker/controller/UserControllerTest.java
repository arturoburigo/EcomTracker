package com.api.ecomtracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.ecomtracker.domain.Role;
import com.api.ecomtracker.domain.User;
import com.api.ecomtracker.dto.user.AdminRegisterRequest;
import com.api.ecomtracker.dto.user.UserRegisterRequest;
import com.api.ecomtracker.exception.BusinessException;
import com.api.ecomtracker.exception.GlobalExceptionHandler;
import com.api.ecomtracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@DisplayName("UserController")
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private UserService userService;

    private MockMvc mockMvc;

    private UserRegisterRequest customerRequest;

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new UserController(userService))
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();
        customerRequest = new UserRegisterRequest();
        customerRequest.setEmail("customer@example.com");
        customerRequest.setUsername("customer");
        customerRequest.setPassword("password123");
    }

    @Test
    @DisplayName("POST /users/register should return 201 with the created user")
    void registerShouldReturnCreated() throws Exception {
        User user =
                new User(
                        1L,
                        "customer@example.com",
                        "customer",
                        "encoded",
                        new Role(1L, Role.RoleName.USER));
        when(userService.registerCustomer(any(UserRegisterRequest.class))).thenReturn(user);

        mockMvc.perform(
                        post("/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("customer@example.com"))
                .andExpect(jsonPath("$.roleName").value("USER"));
    }

    @Test
    @DisplayName("POST /users/register should return 400 for duplicated email")
    void registerShouldReturnBadRequestForDuplicatedEmail() throws Exception {
        when(userService.registerCustomer(any(UserRegisterRequest.class)))
                .thenThrow(new BusinessException("User with this email already exists"));

        mockMvc.perform(
                        post("/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").value("User with this email already exists"));
    }

    @Test
    @DisplayName("POST /users/register should return 400 when the email is invalid")
    void registerShouldReturnBadRequestForInvalidEmail() throws Exception {
        customerRequest.setEmail("not-an-email");

        mockMvc.perform(
                        post("/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users/register/admin should return 201 for a valid admin")
    void registerAdminShouldReturnCreated() throws Exception {
        User admin =
                new User(
                        2L,
                        "admin@example.com",
                        "admin",
                        "encoded",
                        new Role(2L, Role.RoleName.ADMIN));
        when(userService.registerAdmin(any(AdminRegisterRequest.class))).thenReturn(admin);

        mockMvc.perform(
                        post("/users/register/admin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\":\"admin@example.com\",\"username\":\"admin\","
                                                + "\"password\":\"admin123\",\"roleId\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roleName").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /users/register/admin should return 400 when roleId is missing")
    void registerAdminShouldReturnBadRequestWithoutRoleId() throws Exception {
        mockMvc.perform(
                        post("/users/register/admin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }
}
