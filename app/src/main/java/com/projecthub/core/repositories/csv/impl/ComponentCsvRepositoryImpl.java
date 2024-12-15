package com.projecthub.core.repositories.csv.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.projecthub.config.CsvProperties;
import com.projecthub.core.models.Component;
import com.projecthub.core.repositories.csv.ComponentCsvRepository;
import com.projecthub.core.repositories.csv.helper.CsvHelper;
import com.projecthub.core.repositories.csv.helper.CsvFileHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Objects;

@Repository("csvComponentRepository")
@Profile("csv")
public class ComponentCsvRepositoryImpl implements ComponentCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(ComponentCsvRepositoryImpl.class);

    private final Validator validator;
    private final CsvProperties csvProperties;

    public ComponentCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
        this.csvProperties = csvProperties;
        this.validator = validator;
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
            throw new IllegalArgumentException("Component validation failed: " + sb);
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
            CsvFileHelper.backupCSVFile(csvProperties.getComponentsFilepath());
            List<Component> components = readComponentsFromFile();
            components.removeIf(c -> Objects.equals(c.getId(), component.getId()));
            components.add(component);
            String[] columns = {"id", "name", "description", "projectId"};
            CsvHelper.writeBeansToCsv(csvProperties.getComponentsFilepath(), Component.class, components, columns);
            logger.info("Component saved successfully: {}", component);
            return component;
        } catch (Exception e) {
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
            String[] memberFieldsToBindTo = {"id", "name", "description", "projectId"};
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
     * @return an {@code Optional} containing the component if found, or empty if not found
     */
    @Override
    public Optional<Component> findById(UUID id) {
        try {
            List<Component> components = readComponentsFromFile();
            return components.stream()
                    .filter(c -> Objects.equals(c.getId(), id))
                    .findFirst();
        } catch (IOException e) {
            logger.error("Error reading components from CSV", e);
            throw new RuntimeException("Error reading components from CSV", e);
        }
    }

    /**
     * Deletes a component by its ID.
     *
     * @param id the ID of the component to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(UUID id) {
        try {
            CsvFileHelper.backupCSVFile(csvProperties.getComponentsFilepath());
            List<Component> components = readComponentsFromFile();
            components.removeIf(c -> Objects.equals(c.getId(), id));
            writeComponentsToFile(components);
            logger.info("Component deleted successfully: {}", id);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
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
    public List<Component> findByProjectId(UUID projectId) {
        try {
            List<Component> components = readComponentsFromFile();
            return components.stream()
                    .filter(c -> c.getProject() != null && Objects.equals(c.getProject().getId(), projectId))
                    .toList();
        } catch (IOException e) {
            logger.error("Error reading components from CSV", e);
            throw new RuntimeException("Error reading components from CSV", e);
        }
    }

    /**
     * Configures and returns the CSV mapping strategy for {@code Component} objects.
     *
     * @return the configured {@link ColumnPositionMappingStrategy} for {@code Component}
     */
    private ColumnPositionMappingStrategy<Component> getMappingStrategy() {
        ColumnPositionMappingStrategy<Component> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Component.class);
        String[] memberFieldsToBindTo = {"id", "name", "description", "projectId"};
        strategy.setColumnMapping(memberFieldsToBindTo);
        return strategy;
    }

    /**
     * Writes the list of {@code Component} objects to the CSV file.
     *
     * @param components the list of {@code Component} objects to write
     * @throws IOException                    if an I/O error occurs
     * @throws CsvDataTypeMismatchException   if a data type mismatch occurs
     * @throws CsvRequiredFieldEmptyException if a required field is empty
     */
    private void writeComponentsToFile(List<Component> components) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getComponentsFilepath()))) {
            ColumnPositionMappingStrategy<Component> strategy = getMappingStrategy();
            StatefulBeanToCsv<Component> beanToCsv = new StatefulBeanToCsvBuilder<Component>(writer)
                    .withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(components);
        }
    }

    /**
     * Reads and returns all {@code Component} objects from the CSV file.
     *
     * @return a list of {@code Component} objects
     * @throws IOException if an I/O error occurs during reading
     */
    private List<Component> readComponentsFromFile() throws IOException {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getComponentsFilepath()))) {
            ColumnPositionMappingStrategy<Component> strategy = getMappingStrategy();
            return new CsvToBeanBuilder<Component>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        }
    }
}