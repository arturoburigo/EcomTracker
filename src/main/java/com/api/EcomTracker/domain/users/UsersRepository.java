package com.api.EcomTracker.domain.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsersRepository extends JpaRepository<Users, Long> {
    UserDetails findByLogin(String login);
    boolean existsByLogin(String login);
}
