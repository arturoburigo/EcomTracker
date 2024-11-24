package com.api.EcomTracker.controller;

import com.api.EcomTracker.controller.UsersController;
import com.api.EcomTracker.domain.users.Users;
import com.api.EcomTracker.domain.users.UsersRepository;
import com.api.EcomTracker.domain.users.dto.UserRegisterDTO;
import com.api.EcomTracker.domain.users.dto.UserRegisterAdminDTO;
import com.api.EcomTracker.domain.users.roles.Roles;
import com.api.EcomTracker.domain.users.roles.RolesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
@DisplayName("Users Controller Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private RolesRepository rolesRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private UserRegisterDTO userRegisterDTO;
    private UserRegisterAdminDTO adminRegisterDTO;
    private Roles userRole;
    private Roles adminRole;

    @BeforeEach
    void setUp() {
        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@example.com");
        userRegisterDTO.setUsername("testuser");
        userRegisterDTO.setPassword("password123");

        adminRegisterDTO = new UserRegisterAdminDTO();
        adminRegisterDTO.setEmail("admin@example.com");
        adminRegisterDTO.setUsername("adminuser");
        adminRegisterDTO.setPassword("admin123");
        adminRegisterDTO.setRoleId(2L);

        userRole = new Roles(1L, Roles.RoleName.USER);
        adminRole = new Roles(2L, Roles.RoleName.ADMIN);

        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(rolesRepository.findByName(Roles.RoleName.USER)).thenReturn(Optional.of(userRole));
        when(rolesRepository.findById(2L)).thenReturn(Optional.of(adminRole));
    }

    @Nested
    @DisplayName("User Registration Tests")
    class UserRegistrationTests {

        @Test
        @DisplayName("Should successfully register a new user")
        void registerUserSuccessfully() throws Exception {
            when(usersRepository.existsByEmail(userRegisterDTO.getEmail())).thenReturn(false);
            Users savedUser = new Users(1L, userRegisterDTO.getEmail(), userRegisterDTO.getUsername(),
                    "encoded_password", userRole);
            when(usersRepository.save(any(Users.class))).thenReturn(savedUser);

            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRegisterDTO)))
                    .andExpect(status().isCreated());

            verify(usersRepository).save(any(Users.class));
        }

        @Test
        @DisplayName("Should fail when registering user with existing email")
        void registerUserWithExistingEmail() throws Exception {
            when(usersRepository.existsByEmail(userRegisterDTO.getEmail())).thenReturn(true);

            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRegisterDTO)))
                    .andExpect(status().isBadRequest());

            verify(usersRepository, never()).save(any(Users.class));
        }
    }

    @Nested
    @DisplayName("Admin Registration Tests")
    class AdminRegistrationTests {

        @Test
        @DisplayName("Should successfully register a new admin")
        @WithMockUser(roles = "ADMIN")
        void registerAdminSuccessfully() throws Exception {
            when(usersRepository.existsByEmail(adminRegisterDTO.getEmail())).thenReturn(false);
            Users savedUser = new Users(1L, adminRegisterDTO.getEmail(), adminRegisterDTO.getUsername(),
                    "encoded_password", adminRole);
            when(usersRepository.save(any(Users.class))).thenReturn(savedUser);

            mockMvc.perform(post("/auth/register/admin")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adminRegisterDTO)))
                    .andExpect(status().isCreated());

            verify(usersRepository).save(any(Users.class));
        }

        @Test
        @DisplayName("Should fail when registering admin without admin role")
        @WithMockUser(roles = "USER")
        void registerAdminWithoutAdminRole() throws Exception {
            mockMvc.perform(post("/auth/register/admin")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adminRegisterDTO)))
                    .andExpect(status().isForbidden());

            verify(usersRepository, never()).save(any(Users.class));
        }

        @Test
        @DisplayName("Should fail when registering admin with invalid role")
        @WithMockUser(roles = "ADMIN")
        void registerAdminWithInvalidRole() throws Exception {
            when(rolesRepository.findById(adminRegisterDTO.getRoleId())).thenReturn(Optional.empty());

            mockMvc.perform(post("/auth/register/admin")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adminRegisterDTO)))
                    .andExpect(status().isBadRequest());

            verify(usersRepository, never()).save(any(Users.class));
        }
    }
}