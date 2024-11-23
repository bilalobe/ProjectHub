package com.projecthub.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecthub.model.Component;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
    List<Component> findByProjectId(Long projectId);
}