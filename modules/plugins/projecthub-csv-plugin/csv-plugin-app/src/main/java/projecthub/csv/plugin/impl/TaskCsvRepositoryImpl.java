package projecthub.csv.plugin.repository.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import projecthub.csv.plugin.config.CsvProperties;
import projecthub.csv.plugin.helper.CsvFileHelper;
import projecthub.csv.plugin.helper.CsvHelper;
import com.projecthub.core.models.Task;
import com.projecthub.core.repositories.csv.TaskCsvRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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

    private void validateTask(Task task) {
        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Task> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Task validation failed: " + sb);
        }
    }

    @Override
    public Task save(Task task) {
        validateTask(task);
        try {
            CsvFileHelper.backupCSVFile(csvProperties.getTasksFilepath());
            List<Task> tasks = findAll();
            tasks.removeIf(t -> Objects.equals(t.getId(), task.getId()));
            tasks.add(task);

            String[] columns = {"id", "name", "description", "projectId"};
            CsvHelper.writeBeansToCsv(csvProperties.getTasksFilepath(), Task.class, tasks, columns);

            logger.info("Task saved successfully: {}", task);
            return task;
        } catch (Exception e) {
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

    @Override
    public Optional<Task> findById(UUID id) {
        return findAll().stream()
                .filter(t -> Objects.equals(t.getId(), id))
                .findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        try {
            CsvFileHelper.backupCSVFile(csvProperties.getTasksFilepath());
            List<Task> tasks = findAll();
            tasks.removeIf(t -> Objects.equals(t.getId(), id));

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

    @Override
    public List<Task> findByProjectId(UUID projectId) {
        return findAll().stream()
                .filter(t -> t.getProject() != null && Objects.equals(t.getProject().getId(), projectId))
                .toList();
    }
}