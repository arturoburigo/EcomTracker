package com.api.EcomTracker.domain.users.roles;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(Roles.RoleName name);
}