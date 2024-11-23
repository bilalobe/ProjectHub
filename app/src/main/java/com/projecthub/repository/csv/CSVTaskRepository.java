package com.projecthub.repository.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.projecthub.model.Task;
import com.projecthub.repository.custom.CustomTaskRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.*;
import java.nio.file.*;
import java.util.Set;

/**
 * CSV implementation of the CustomTaskRepository interface.
 */
@Repository
public abstract class CSVTaskRepository implements CustomTaskRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVTaskRepository.class);
    private final Validator validator;

    @Value("${app.tasks.filepath}")
    private String tasksFilePath;

    public CSVTaskRepository() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private void backupCSVFile(String filePath) throws IOException {
        java.nio.file.Path source = java.nio.file.Path.of(filePath);
        java.nio.file.Path backup = Paths.get(filePath + ".backup");
        Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created for file: {}", filePath);
    }

    private void validateTask(Task task) {
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Task> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Task validation failed: " + sb.toString());
        }
    }

    @Override
    public List<Task> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(tasksFilePath))) {
            ColumnPositionMappingStrategy<Task> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Task.class);
            strategy.setColumnMapping("id", "name", "description", "status", "dueDate", "assignedUserId", "projectId");

            return new CsvToBeanBuilder<Task>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException("Error reading tasks from CSV", e);
        }
    }

    @Override
    public Task save(Task task) {
        validateTask(task);
        try {
            backupCSVFile(tasksFilePath);
            List<Task> tasks = findAll();
            tasks.removeIf(t -> t.getId().equals(task.getId()));
            tasks.add(task);

            try (CSVWriter writer = new CSVWriter(new FileWriter(tasksFilePath))) {
                ColumnPositionMappingStrategy<Task> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Task.class);
                strategy.setColumnMapping("id", "name", "description", "status", "dueDate", "assignedUserId", "projectId");

                new StatefulBeanToCsvBuilder<Task>(writer)
                        .withMappingStrategy(strategy)
                        .build()
                        .write(tasks);
            }
            logger.info("Task saved successfully: {}", task);
            return task;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error saving task to CSV", e);
            throw new RuntimeException("Error saving task to CSV", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            backupCSVFile(tasksFilePath);
            List<Task> tasks = findAll();
            tasks.removeIf(t -> t.getId().equals(id));

            try (CSVWriter writer = new CSVWriter(new FileWriter(tasksFilePath))) {
                ColumnPositionMappingStrategy<Task> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Task.class);
                strategy.setColumnMapping("id", "name", "description", "status", "dueDate", "assignedUserId", "projectId");

                new StatefulBeanToCsvBuilder<Task>(writer)
                        .withMappingStrategy(strategy)
                        .build()
                        .write(tasks);
            }
            logger.info("Task deleted successfully: {}", id);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting task from CSV", e);
            throw new RuntimeException("Error deleting task from CSV", e);
        }
    }

    @Override
    public List<Task> findByProjectId(Long projectId) {
        return findAll().stream()
                .filter(task -> task.getProject() != null && task.getProject().getId().equals(projectId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByAssignedUserId(Long userId) {
        return findAll().stream()
                .filter(task -> task.getAssignedUser() != null && task.getAssignedUser().getId().equals(userId))
                .collect(Collectors.toList());
    }
}