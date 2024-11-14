package com.api.EcomTracker.domain.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsersRepository extends JpaRepository<Users, Long> {
    UserDetails findByUsername(String username);
    UserDetails findByEmail(String email);
    boolean existsByEmail(String email);
}
