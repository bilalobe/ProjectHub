package com.projecthub.repository.custom;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.projecthub.model.Task;

@Repository
public interface CustomTaskRepository{
    List<Task> findByProjectId(Long projectId);
    List<Task> findByAssignedUserId(Long userId);
    Task save(Task task);
    List<Task> findAll();
    Optional<Task> findById(Long id);
    void deleteById(Long id);
}