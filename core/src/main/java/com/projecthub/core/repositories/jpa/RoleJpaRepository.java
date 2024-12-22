package com.projecthub.core.repositories.jpa;

import com.projecthub.core.entities.Role;
import com.projecthub.core.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleJpaRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(RoleType name);
}
