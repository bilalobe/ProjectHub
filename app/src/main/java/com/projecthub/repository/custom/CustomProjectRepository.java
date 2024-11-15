package com.projecthub.repository.custom;

import java.util.List;

import com.projecthub.model.Project;

public interface CustomProjectRepository {
    List<Project> findAll();
    List<Project> findAllByTeamId(Long teamId);
    Project findProjectWithComponentsById(Long projectId);
    <S extends Project> S save(S project);
    void deleteById(Long projectId);
}