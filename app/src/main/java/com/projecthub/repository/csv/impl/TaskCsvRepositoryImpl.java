package com.projecthub.repository.csv.impl;

import com.projecthub.model.Task;
import com.projecthub.repository.csv.TaskCsvRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.projecthub.config.CsvProperties;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Repository("csvTaskRepository")
@Profile("csv")
public class TaskCsvRepositoryImpl implements TaskCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(TaskCsvRepositoryImpl.class);

    private final Validator validator;
    private final CsvProperties csvProperties;

    public TaskCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
        this.csvProperties = csvProperties;
        this.validator = validator;
    }

    private void backupCSVFile(String filePath) throws IOException {
        Path source = Path.of(filePath);
        Path backup = Path.of(filePath + ".backup");
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
    public Task save(Task task) {
        validateTask(task);
        try {
            backupCSVFile(csvProperties.getTasksFilepath());
            List<Task> tasks = findAll();
            tasks.removeIf(t -> t.getId().equals(task.getId()));
            tasks.add(task);

            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getTasksFilepath()))) {
                ColumnPositionMappingStrategy<Task> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Task.class);
                String[] memberFieldsToBindTo = {"id", "name", "description", "projectId"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<Task> beanToCsv = new StatefulBeanToCsvBuilder<Task>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(tasks);
            }

            logger.info("Task saved successfully: {}", task);
            return task;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error saving task to CSV", e);
            throw new RuntimeException("Error saving task to CSV", e);
        }
    }

    @Override
    public List<Task> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getTasksFilepath()))) {
            ColumnPositionMappingStrategy<Task> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Task.class);
            String[] memberFieldsToBindTo = {"id", "name", "description", "projectId"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            return new CsvToBeanBuilder<Task>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            logger.error("Error reading tasks from CSV", e);
            throw new RuntimeException("Error reading tasks from CSV", e);
        }
    }

    public Optional<Task> findById(UUID id) {
        return findAll().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    public void deleteById(UUID id) {
        try {
            backupCSVFile(csvProperties.getTasksFilepath());
            List<Task> tasks = findAll();
            tasks.removeIf(t -> t.getId().equals(id));

            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getTasksFilepath()))) {
                ColumnPositionMappingStrategy<Task> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Task.class);
                String[] memberFieldsToBindTo = {"id", "name", "description", "projectId"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<Task> beanToCsv = new StatefulBeanToCsvBuilder<Task>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(tasks);
            }

            logger.info("Task deleted successfully: {}", id);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting task from CSV", e);
            throw new RuntimeException("Error deleting task from CSV", e);
        }
    }

    public List<Task> findByProjectId(UUID projectId) {
        return findAll().stream()
                .filter(t -> t.getProject().getId().equals(projectId))
                .collect(Collectors.toList());
    }

    public List<Task> findByAssignedUserId(UUID userId) {
        return findAll().stream()
                .filter(t -> t.getAssignedUser().getId().equals(userId))
                .collect(Collectors.toList());
    }
}