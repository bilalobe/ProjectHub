package com.projecthub.repository.csv.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.projecthub.config.CsvProperties;
import com.projecthub.model.Component;
import com.projecthub.repository.csv.ComponentCsvRepository;
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

@Repository("csvComponentRepository")
public class ComponentCsvRepositoryImpl implements ComponentCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(ComponentCsvRepository.class);

    private final Validator validator;
    private final CsvProperties csvProperties;

    public ComponentCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
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
     * Validates a {@link Component} object.
     *
     * @param component the {@code Component} object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateComponent(Component component) {
        Set<ConstraintViolation<Component>> violations = validator.validate(component);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Component> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Component validation failed: " + sb.toString());
        }
    }

    /**
     * Saves a component to the CSV file after validation and backup.
     *
     * @param component the {@code Component} object to save
     * @return the saved {@code Component} object
     * @throws RuntimeException if an error occurs during saving
     */
    @Override
    public Component save(Component component) {
        validateComponent(component);
        try {
            backupCSVFile(csvProperties.getComponentsFilepath());
            List<Component> components = findAll();
            components.removeIf(c -> c.getId().equals(component.getId()));
            components.add(component);
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getComponentsFilepath()))) {
                ColumnPositionMappingStrategy<Component> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Component.class);
                String[] memberFieldsToBindTo = { "id", "name", "description", "projectId" };
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Component> beanToCsv = new StatefulBeanToCsvBuilder<Component>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(components);
            }
            logger.info("Component saved successfully: {}", component);
            return component;
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException
                | com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error saving component to CSV", e);
            throw new RuntimeException("Error saving component to CSV", e);
        }
    }

    /**
     * Retrieves all components from the CSV file.
     *
     * @return a list of {@code Component} objects
     */
    @Override
    public List<Component> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getComponentsFilepath()))) {
            ColumnPositionMappingStrategy<Component> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Component.class);
            String[] memberFieldsToBindTo = { "id", "name", "description", "projectId" };
            strategy.setColumnMapping(memberFieldsToBindTo);
            return new CsvToBeanBuilder<Component>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            logger.error("Error reading components from CSV", e);
            throw new RuntimeException("Error reading components from CSV", e);
        }
    }

    /**
     * Finds a component by its ID.
     *
     * @param id the ID of the component
     * @return an {@code Optional} containing the component if found, or empty if
     *         not found
     */
    @Override
    public Optional<Component> findById(Long id) {
        return findAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    /**
     * Deletes a component by its ID.
     *
     * @param id the ID of the component to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(Long id) {
        try {
            backupCSVFile(csvProperties.getComponentsFilepath());
            List<Component> components = findAll();
            components.removeIf(c -> c.getId().equals(id));
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getComponentsFilepath()))) {
                ColumnPositionMappingStrategy<Component> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Component.class);
                String[] memberFieldsToBindTo = { "id", "name", "description", "projectId" };
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Component> beanToCsv = new StatefulBeanToCsvBuilder<Component>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(components);
            }
            logger.info("Component deleted successfully: {}", id);
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException
                | com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting component from CSV", e);
            throw new RuntimeException("Error deleting component from CSV", e);
        }
    }

    /**
     * Finds components by project ID.
     *
     * @param projectId the ID of the project
     * @return a list of {@code Component} objects belonging to the project
     */
    @Override
    public List<Component> findByProjectId(Long projectId) {
        return findAll().stream()
                .filter(c -> c.getProject().getId().equals(projectId))
                .toList();
    }
}