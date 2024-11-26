package com.projecthub.repository.csv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.projecthub.repository.custom.CustomProjectRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * CSV implementation of the {@link CustomProjectRepository} interface.
 */
@Repository("csvProjectRepository")
public abstract class CSVProjectRepository implements CustomProjectRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVProjectRepository.class);
    private final Validator validator;

    @Value("${app.projects.filepath}")
    private String projectsFilePath;

    /**
     * Constructs a new {@code CSVProjectRepository}.
     */
    public CSVProjectRepository() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
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
            throw new IllegalArgumentException("Project validation failed: " + sb.toString());
        }
    }

    /**
     * Retrieves all projects from the CSV file.
     *
     * @return a list of {@code Project} objects
     */
    @Override
    @NonNull
    public List<Project> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(projectsFilePath))) {
            ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Project.class);
            String[] memberFieldsToBindTo = {"id", "name", "description", "team"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            return new CsvToBeanBuilder<Project>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException("Error reading projects from CSV", e);
        }
    }

    /**
     * Finds all projects associated with a specific team ID.
     *
     * @param teamId the ID of the team
     * @return a list of projects belonging to the specified team
     */
    @Override
    public List<Project> findAllByTeamId(Long teamId) {
        return findAll().stream()
                .filter(project -> project.getTeam() != null && project.getTeam().getId().equals(teamId))
                .toList();
    }

    /**
     * Finds a project along with its components by project ID.
     *
     * @param projectId the ID of the project
     * @return an {@code Optional} containing the project if found, or empty if not found
     */
    @Override
    public Optional<Project> findProjectWithComponentsById(Long projectId) {
        return findAll().stream()
                .filter(project -> project.getId().equals(projectId))
                .findFirst();
    }

    /**
     * Saves a project to the CSV file after validation and backup.
     *
     * @param project the {@code Project} object to save
     * @return the saved {@code Project} object
     * @throws RuntimeException if an error occurs during saving
     */
    @Override
    public @NonNull <S extends Project> S save(@NonNull S project) {
        validateProject(project);
        try {
            backupCSVFile(projectsFilePath);
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

            logger.info("Project saved successfully: {}", project);
            return project;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error saving project to CSV", e);
            throw new RuntimeException("Error saving project to CSV", e);
        }
    }

    /**
     * Deletes a project by its ID.
     *
     * @param projectId the ID of the project to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(@NonNull Long projectId) {
        try {
            backupCSVFile(projectsFilePath);
            List<Project> projects = findAll();
            projects.removeIf(p -> p.getId().equals(projectId));

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

            logger.info("Project deleted successfully: {}", projectId);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting project from CSV", e);
            throw new RuntimeException("Error deleting project from CSV", e);
        }
    }
}