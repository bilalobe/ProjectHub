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
import org.springframework.beans.factory.annotation.Value;
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
import com.projecthub.model.Component;
import com.projecthub.repository.custom.CustomComponentRepository;

/**
 * CSV implementation of the CustomComponentRepository interface.
 */
@Repository("csvComponentRepository")
public abstract class CSVComponentRepository implements CustomComponentRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVComponentRepository.class);
    private final Validator validator;

    @Value("${app.components.filepath}")
    private String componentsFilePath;

    public CSVComponentRepository() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private void backupCSVFile(String filePath) throws IOException {
        Path source = Path.of(filePath);
        Path backup = Path.of(filePath + ".backup");
        Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created for file: {}", filePath);
    }

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
     * @param component the Component object to save
     * @return the saved Component object
     */
    public Component save(Component component) {
        validateComponent(component);
        try {
            backupCSVFile(componentsFilePath);
            List<Component> components = findAll();
            components.removeIf(c -> c.getId().equals(component.getId()));
            components.add(component);

            try (CSVWriter writer = new CSVWriter(new FileWriter(componentsFilePath))) {
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

    @Override
    public List<Component> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(componentsFilePath))) {
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
     */
    @Override
    public void deleteById(Long componentId) {
        try {
            backupCSVFile(componentsFilePath);
            List<Component> components = findAll();
            components.removeIf(component -> component.getId().equals(componentId));

            try (CSVWriter writer = new CSVWriter(new FileWriter(componentsFilePath))) {
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