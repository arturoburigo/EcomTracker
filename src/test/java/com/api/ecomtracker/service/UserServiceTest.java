package com.api.ecomtracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.api.ecomtracker.domain.Role;
import com.api.ecomtracker.domain.User;
import com.api.ecomtracker.dto.user.AdminRegisterRequest;
import com.api.ecomtracker.dto.user.UserRegisterRequest;
import com.api.ecomtracker.exception.BusinessException;
import com.api.ecomtracker.repository.RoleRepository;
import com.api.ecomtracker.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    private final Role userRole = new Role(1L, Role.RoleName.USER);

    private final Role adminRole = new Role(2L, Role.RoleName.ADMIN);

    @Mock private UserRepository userRepository;

    @Mock private RoleRepository roleRepository;

    @Mock private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    private UserRegisterRequest customerRequest;

    private AdminRegisterRequest adminRequest;

    @BeforeEach
    void setUp() {
        customerRequest = new UserRegisterRequest();
        customerRequest.setEmail("customer@example.com");
        customerRequest.setUsername("customer");
        customerRequest.setPassword("password123");

        adminRequest = new AdminRegisterRequest();
        adminRequest.setEmail("admin@example.com");
        adminRequest.setUsername("admin");
        adminRequest.setPassword("admin123");
        adminRequest.setRoleId(2L);
    }

    @Test
    @DisplayName("registerCustomer should encode the password and save the user with USER role")
    void registerCustomerShouldSaveUser() {
        when(userRepository.existsByEmail(customerRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(call -> call.getArgument(0));

        User saved = userService.registerCustomer(customerRequest);

        assertThat(saved.getEmail()).isEqualTo("customer@example.com");
        assertThat(saved.getUsername()).isEqualTo("customer");
        assertThat(saved.getPassword()).isEqualTo("encoded");
        assertThat(saved.getRole()).isEqualTo(userRole);
    }

    @Test
    @DisplayName("registerCustomer should reject duplicated emails")
    void registerCustomerShouldRejectDuplicatedEmail() {
        when(userRepository.existsByEmail(customerRequest.getEmail())).thenReturn(true);
        when(roleRepository.findByName(Role.RoleName.USER)).thenReturn(Optional.of(userRole));

        assertThatThrownBy(() -> userService.registerCustomer(customerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("registerAdmin should save the user when the role is ADMIN")
    void registerAdminShouldSaveAdminUser() {
        when(roleRepository.findById(2L)).thenReturn(Optional.of(adminRole));
        when(userRepository.existsByEmail(adminRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(call -> call.getArgument(0));

        User saved = userService.registerAdmin(adminRequest);

        assertThat(saved.getRole().isAdmin()).isTrue();
    }

    @Test
    @DisplayName("registerAdmin should reject roles that are not ADMIN")
    void registerAdminShouldRejectNonAdminRole() {
        adminRequest.setRoleId(1L);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(userRole));

        assertThatThrownBy(() -> userService.registerAdmin(adminRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("admin");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("registerAdmin should fail when the role does not exist")
    void registerAdminShouldFailWhenRoleMissing() {
        when(roleRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.registerAdmin(adminRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Role not found");
    }
}
