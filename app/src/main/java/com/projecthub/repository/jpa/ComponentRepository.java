package com.projecthub.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecthub.model.Component;
import com.projecthub.repository.custom.CustomComponentRepository;

@Repository("postgresComponentRepository")
public interface ComponentRepository extends JpaRepository<Component, Long>, CustomComponentRepository {
    // Custom query methods can be added here
}