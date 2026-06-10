package com.api.ecomtracker.service;

import com.api.ecomtracker.domain.Role;
import com.api.ecomtracker.domain.User;
import com.api.ecomtracker.dto.user.AdminRegisterRequest;
import com.api.ecomtracker.dto.user.UserRegisterRequest;
import com.api.ecomtracker.exception.BusinessException;
import com.api.ecomtracker.repository.RoleRepository;
import com.api.ecomtracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerCustomer(UserRegisterRequest request) {
        Role role = findRoleByName(Role.RoleName.USER);
        return register(request, role);
    }

    @Transactional
    public User registerAdmin(AdminRegisterRequest request) {
        Role role =
                roleRepository
                        .findById(request.getRoleId())
                        .orElseThrow(() -> new BusinessException("Role not found"));
        if (!role.isAdmin()) {
            throw new BusinessException("This endpoint can only be used to create admin users");
        }
        return register(request, role);
    }

    private User register(UserRegisterRequest request, Role role) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("User with this email already exists");
        }
        User user =
                new User(
                        request.getEmail(),
                        request.getUsername(),
                        passwordEncoder.encode(request.getPassword()),
                        role);
        return userRepository.save(user);
    }

    private Role findRoleByName(Role.RoleName name) {
        return roleRepository
                .findByName(name)
                .orElseThrow(() -> new BusinessException("Role not found in the database"));
    }
}
