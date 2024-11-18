package com.projecthub.service;

import com.projecthub.model.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.projecthub.repository.custom.CustomTaskRepository;

@Service
public class TaskService {

    private final CustomTaskRepository CustomTaskRepository;

    @Autowired
    public TaskService(CustomTaskRepository CustomTaskRepository) {
        this.CustomTaskRepository = CustomTaskRepository;
    }

    public Task saveTask(Task task) {
        return CustomTaskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return CustomTaskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return CustomTaskRepository.findById(id);
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        return CustomTaskRepository.findByProjectId(projectId);
    }

    public List<Task> getTasksByAssignedUserId(Long userId) {
        return CustomTaskRepository.findByAssignedUserId(userId);
    }

    public void deleteTask(Long id) {
        CustomTaskRepository.deleteById(id);
    }
}