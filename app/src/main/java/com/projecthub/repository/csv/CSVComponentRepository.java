package com.projecthub.repository.csv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.projecthub.config.CSVProperties;
import com.projecthub.model.Component;
import com.projecthub.repository.custom.CustomComponentRepository;

/**
 * CSV implementation of the {@link CustomComponentRepository} interface.
 */
@Repository("csvComponentRepository")
public abstract class CSVComponentRepository implements CustomComponentRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVComponentRepository.class);
    private final Validator validator;
    private final CSVProperties csvProperties;

    /**
     * Constructs a new {@code CSVComponentRepository}.
     *
     * @param csvProperties the {@code CSVProperties} object
     */
    public CSVComponentRepository(CSVProperties csvProperties) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        this.csvProperties = csvProperties;
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
                String[] memberFieldsToBindTo = {"id", "name", "description", "projectId"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<Component> beanToCsv = new StatefulBeanToCsvBuilder<Component>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(components);
            }
            logger.info("Component saved successfully: {}", component);
            return component;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
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
            throw new RuntimeException("Error reading components from CSV", e);
        }
    }

    /**
     * Deletes a component by its ID.
     *
     * @param componentId the ID of the component to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(Long componentId) {
        try {
            backupCSVFile(csvProperties.getComponentsFilepath());
            List<Component> components = findAll();
            components.removeIf(component -> component.getId().equals(componentId));

            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getComponentsFilepath()))) {
                ColumnPositionMappingStrategy<Component> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Component.class);
                String[] memberFieldsToBindTo = {"id", "name", "description", "projectId"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<Component> beanToCsv = new StatefulBeanToCsvBuilder<Component>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(components);
            }
            logger.info("Component deleted successfully: {}", componentId);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting component from CSV", e);
            throw new RuntimeException("Error deleting component from CSV", e);
        }
    }
}