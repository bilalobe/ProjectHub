package com.projecthub.repository.csv.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.projecthub.config.CsvProperties;
import com.projecthub.repository.csv.ProjectCsvRepository;
import com.projecthub.model.Project;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository("csvProjectRepository")
public class ProjectCsvRepositoryImpl implements ProjectCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProjectCsvRepositoryImpl.class);

    private final Validator validator;
    private final CsvProperties csvProperties;

    public ProjectCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
        this.csvProperties = csvProperties;
        this.validator = validator;
    }

    /**
     * Creates a backup of the CSV file.
     *
     * @param filePath the path of the CSV file to back up
     * @throws IOException if an I/O error occurs during backup
     */
    private void backupCSVFile(String filePath) throws IOException {
        Path source = Path.of(filePath);
        Path backup = Path.of(filePath + ".backup");
        Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created for file: {}", filePath);
    }

    /**
     * Validates a {@link Project} object.
     *
     * @param project the {@code Project} object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateProject(Project project) {
        Set<ConstraintViolation<Project>> violations = validator.validate(project);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Project> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Project validation failed: " + sb);
        }
    }

    /**
     * Saves a project to the CSV file after validation and backup.
     *
     * @param project the {@code Project} object to save
     * @return the saved {@code Project} object
     * @throws RuntimeException if an error occurs during saving
     */
    @Override
    public Project save(Project project) {
        validateProject(project);
        try {
            backupCSVFile(csvProperties.getProjectsFilepath());
            List<Project> projects = findAll();
            projects.removeIf(p -> p.getId().equals(project.getId()));
            projects.add(project);
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getProjectsFilepath()))) {
                ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Project.class);
                String[] memberFieldsToBindTo = {"id", "name", "description", "teamId", "deadline", "startDate", "endDate", "status"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Project> beanToCsv = new StatefulBeanToCsvBuilder<Project>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(projects);
            }
            logger.info("Project saved successfully: {}", project);
            return project;
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException | com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error saving project to CSV", e);
            throw new RuntimeException("Error saving project to CSV", e);
        }
    }

    /**
     * Retrieves all projects from the CSV file.
     *
     * @return a list of {@code Project} objects
     */
    @Override
    public List<Project> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getProjectsFilepath()))) {
            ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Project.class);
            String[] memberFieldsToBindTo = {"id", "name", "description", "teamId", "deadline", "startDate", "endDate", "status"};
            strategy.setColumnMapping(memberFieldsToBindTo);
            return new CsvToBeanBuilder<Project>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            logger.error("Error reading projects from CSV", e);
            throw new RuntimeException("Error reading projects from CSV", e);
        }
    }

    /**
     * Finds a project by its ID.
     *
     * @param id the ID of the project
     * @return an {@code Optional} containing the project if found, or empty if not found
     */
    @Override
    public Optional<Project> findById(UUID id) {
        return findAll().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    /**
     * Deletes a project by its ID.
     *
     * @param id the ID of the project to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(UUID id) {
        try {
            backupCSVFile(csvProperties.getProjectsFilepath());
            List<Project> projects = findAll();
            projects.removeIf(p -> p.getId().equals(id));
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getProjectsFilepath()))) {
                ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Project.class);
                String[] memberFieldsToBindTo = {"id", "name", "description", "teamId", "deadline", "startDate", "endDate", "status"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Project> beanToCsv = new StatefulBeanToCsvBuilder<Project>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(projects);
            }
            logger.info("Project deleted successfully: {}", id);
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException | com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting project from CSV", e);
            throw new RuntimeException("Error deleting project from CSV", e);
        }
    }

    /**
     * Finds projects by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of {@code Project} objects belonging to the team
     */
    @Override
    public List<Project> findAllByTeamId(UUID teamId) {
        return findAll().stream()
                .filter(p -> p.getTeam().getId().equals(teamId))
                .toList();
    }
}