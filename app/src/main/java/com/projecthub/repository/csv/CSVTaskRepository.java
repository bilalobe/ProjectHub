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

/**
 * CSV implementation of the CustomTaskRepository interface.
 */
@Repository
public abstract class CSVTaskRepository implements CustomTaskRepository {

    @Value("${app.tasks.filepath}")
    private String tasksFilePath;

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
        List<Task> tasks = findAll();
        tasks.removeIf(t -> t.getId().equals(task.getId()));
        tasks.add(task);

        try (CSVWriter writer = new CSVWriter(new FileWriter(tasksFilePath))) {
            ColumnPositionMappingStrategy<Task> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Task.class);
            strategy.setColumnMapping("id", "name", "description", "status", "dueDate", "assignedUserId", "projectId");

            try {
                new StatefulBeanToCsvBuilder<Task>(writer)
                        .withMappingStrategy(strategy)
                        .build()
                        .write(tasks);
            } catch (CsvDataTypeMismatchException ex) {
            } catch (CsvRequiredFieldEmptyException ex) {
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing tasks to CSV", e);
        }

        return task;
    }

    @Override
    public void deleteById(Long id) {
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
        } catch (IOException e) {
            throw new RuntimeException("Error writing tasks to CSV", e);
        } catch (CsvDataTypeMismatchException ex) {
        } catch (CsvRequiredFieldEmptyException ex) {
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