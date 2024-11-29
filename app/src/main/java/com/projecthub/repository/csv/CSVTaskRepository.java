package com.projecthub.repository.csv;

import com.projecthub.config.CSVProperties;
import com.projecthub.model.Task;
import com.projecthub.repository.custom.CustomTaskRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * CSV implementation of the {@link CustomTaskRepository} interface.
 */
@Repository("csvTaskRepository")
public abstract class CSVTaskRepository implements CustomTaskRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVTaskRepository.class);

    private final Validator validator;
    private final CSVProperties csvProperties;


    public CSVTaskRepository(CSVProperties csvProperties, Validator validator) {
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
     * Validates a {@link Task} object.
     *
     * @param task the {@code Task} object to validate
     * @throws IllegalArgumentException if validation fails
     */
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

    /**
     * Saves a task to the CSV file after validation and backup.
     *
     * @param task the {@code Task} object to save
     * @return the saved {@code Task} object
     * @throws RuntimeException if an error occurs during saving
     */
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

    /**
     * Retrieves all tasks from the CSV file.
     *
     * @return a list of {@code Task} objects
     */
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

    /**
     * Finds a task by its ID.
     *
     * @param id the ID of the task
     * @return an {@code Optional} containing the task if found, or empty if not found
     */
    @Override
    public Optional<Task> findById(Long id) {
        return findAll().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(Long id) {
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
}