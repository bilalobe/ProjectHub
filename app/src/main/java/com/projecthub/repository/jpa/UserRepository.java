package com.projecthub.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecthub.model.User;
import com.projecthub.repository.custom.CustomUserRepository;

@Repository("postgresUserRepository")
public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
    // Custom query methods can be added here
}