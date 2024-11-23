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

@Repository("csvProjectRepository")
public abstract class CSVProjectRepository implements CustomProjectRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVProjectRepository.class);
    private final Validator validator;

    @Value("${app.projects.filepath}")
    private String projectsFilePath;

    public CSVProjectRepository() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private void backupCSVFile(String filePath) throws IOException {
        Path source = Path.of(filePath);
        Path backup = Path.of(filePath + ".backup");
        Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created for file: {}", filePath);
    }

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

    @Override
    public @NonNull List<Project> findAll() {
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

    @Override
    public List<Project> findAllByTeamId(Long teamId) {
        return findAll().stream()
                .filter(project -> project.getTeam() != null && project.getTeam().getId().equals(teamId))
                .toList();
    }

    @Override
    public Optional<Project> findProjectWithComponentsById(Long projectId) {
        return findAll().stream()
                .filter(project -> project.getId().equals(projectId))
                .findFirst();
    }

    @Override
    public @NonNull <S extends Project> S save(S project) {
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