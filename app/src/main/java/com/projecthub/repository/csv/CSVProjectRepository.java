package com.projecthub.repository.csv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.projecthub.model.Project;
import com.projecthub.repository.jpa.ProjectRepository;

@Repository("csvProjectRepository")
public abstract class CSVProjectRepository implements ProjectRepository {

    @Value("${app.projects.filepath}")
    private String projectsFilePath;

    @Override
    public @NonNull List<Project> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(projectsFilePath))) {
            ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Project.class);
            String[] memberFieldsToBindTo = {"id", "name", "description", "team"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<Project> projects = new CsvToBeanBuilder<Project>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();

            return projects;
        } catch (IOException e) {
            throw new RuntimeException("Error reading projects from CSV", e);
        }
    }

    @Override
    public List<Project> findAllByTeamId(Long teamId) {
        try (CSVReader reader = new CSVReader(new FileReader(projectsFilePath))) {
            ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Project.class);
            String[] memberFieldsToBindTo = {"id", "name", "description", "team"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<Project> projects = new CsvToBeanBuilder<Project>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();

            return projects.stream()
                    .filter(project -> project.getTeam() != null && project.getTeam().getId().equals(teamId))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error reading projects from CSV", e);
        }
    }

    @Override
    public Project findProjectWithComponentsById(Long projectId) {
        try (CSVReader reader = new CSVReader(new FileReader(projectsFilePath))) {
            ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Project.class);
            String[] memberFieldsToBindTo = {"id", "name", "description", "team"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<Project> projects = new CsvToBeanBuilder<Project>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();

            return projects.stream()
                    .filter(project -> project.getId().equals(projectId))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            throw new RuntimeException("Error reading projects from CSV", e);
        }
    }

    @SuppressWarnings("null")
    @Override
    public @NonNull <S extends Project> S save(S project) {
        try {
            List<Project> projects = findAll();
            projects.removeIf(p -> p.getId().equals(project.getId()));
            projects.add(project);

            try (CSVWriter writer = new CSVWriter(new FileWriter(projectsFilePath))) {
                ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Project.class);
                String[] memberFieldsToBindTo = {"id", "name", "description", "team"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<Project> beanToCsv = new StatefulBeanToCsvBuilder<Project>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(projects);
            }

            return project;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Error saving project to CSV", e);
        }
    }

}