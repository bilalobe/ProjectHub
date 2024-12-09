package com.projecthub.repository.csv.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.projecthub.config.CsvProperties;
import com.projecthub.model.Cohort;
import com.projecthub.repository.csv.CohortCsvRepository;
import com.projecthub.repository.csv.helper.CsvHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of {@link CohortCsvRepository} that manages Cohort data using CSV files.
 * <p>
 * This repository provides methods to perform CRUD operations on Cohort data stored in a CSV file.
 * It handles data validation, backup creation, and CSV read/write operations.
 * </p>
 */
@Repository("csvCohortRepository")
@Profile("csv")
public class CohortCsvRepositoryImpl implements CohortCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(CohortCsvRepositoryImpl.class);

    private final CsvProperties csvProperties;
    private final Validator validator;

    /**
     * Constructs a new {@code CohortCsvRepositoryImpl} with the specified CSV properties and validator.
     *
     * @param csvProperties the CSV configuration properties
     * @param validator     the validator for Cohort objects
     */
    public CohortCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
        this.csvProperties = csvProperties;
        this.validator = validator;
    }

    /**
     * Saves the given {@code Cohort} to the CSV file after validation and backup.
     *
     * @param cohort the {@code Cohort} object to save
     * @return the saved {@code Cohort} object
     * @throws RuntimeException if an error occurs during saving
     */
    @Override
    public Cohort save(Cohort cohort) {
        validateCohort(cohort);
        try {
            backupCSVFile(csvProperties.getCohortsFilepath());
            List<Cohort> cohorts = findAll();
            cohorts.removeIf(c -> c.getId().equals(cohort.getId()));
            cohorts.add(cohort);
            String[] columns = {"id", "name", "schoolId"};
            CsvHelper.writeBeansToCsv(csvProperties.getCohortsFilepath(), Cohort.class, cohorts, columns);
            logger.info("Cohort saved successfully: {}", cohort);
            return cohort;
        } catch (Exception e) {
            logger.error("Error saving cohort to CSV", e);
            throw new RuntimeException("Error saving cohort to CSV", e);
        }
    }

    /**
     * Retrieves all {@code Cohort} objects from the CSV file.
     *
     * @return a list of all {@code Cohort} objects
     * @throws RuntimeException if an error occurs during reading
     */
    @Override
    public List<Cohort> findAll() {
        try {
            return readCohortsFromFile();
        } catch (IOException e) {
            logger.error("Error reading cohorts from CSV", e);
            throw new RuntimeException("Error reading cohorts from CSV", e);
        }
    }

    /**
     * Finds a {@code Cohort} by its unique identifier.
     *
     * @param id the UUID of the cohort
     * @return an {@code Optional} containing the found {@code Cohort}, or empty if not found
     */
    @Override
    public Optional<Cohort> findById(UUID id) {
        return findAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    /**
     * Deletes a {@code Cohort} by its unique identifier.
     *
     * @param id the UUID of the cohort to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(UUID id) {
        try {
            backupCSVFile(csvProperties.getCohortsFilepath());
            List<Cohort> cohorts = findAll();
            cohorts.removeIf(c -> c.getId().equals(id));
            writeCohortsToFile(cohorts);
            logger.info("Cohort deleted successfully: {}", id);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting cohort from CSV", e);
            throw new RuntimeException("Error deleting cohort from CSV", e);
        }
    }

    /**
     * Finds all {@code Cohort} objects associated with a specific school ID.
     *
     * @param schoolId the UUID of the school
     * @return a list of {@code Cohort} objects belonging to the specified school
     */
    @Override
    public List<Cohort> findBySchoolId(UUID schoolId) {
        return findAll().stream()
                .filter(c -> c.getSchool().getId().equals(schoolId))
                .toList();
    }

    /**
     * Creates a backup of the specified CSV file.
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
     * Validates the given {@code Cohort} object using the validator.
     *
     * @param cohort the {@code Cohort} object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateCohort(Cohort cohort) {
        Set<ConstraintViolation<Cohort>> violations = validator.validate(cohort);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Cohort> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Cohort validation failed: " + sb);
        }
    }

    /**
     * Configures and returns the CSV mapping strategy for {@code Cohort} objects.
     *
     * @return the configured {@link ColumnPositionMappingStrategy} for {@code Cohort}
     */
    private ColumnPositionMappingStrategy<Cohort> getMappingStrategy() {
        ColumnPositionMappingStrategy<Cohort> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Cohort.class);
        String[] memberFieldsToBindTo = {"id", "name", "schoolId"};
        strategy.setColumnMapping(memberFieldsToBindTo);
        return strategy;
    }

    /**
     * Writes the list of {@code Cohort} objects to the CSV file.
     *
     * @param cohorts the list of {@code Cohort} objects to write
     * @throws IOException                           if an I/O error occurs
     * @throws CsvDataTypeMismatchException          if a data type mismatch occurs
     * @throws CsvRequiredFieldEmptyException        if a required field is empty
     */
    private void writeCohortsToFile(List<Cohort> cohorts) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getCohortsFilepath()))) {
            ColumnPositionMappingStrategy<Cohort> strategy = getMappingStrategy();
            StatefulBeanToCsv<Cohort> beanToCsv = new StatefulBeanToCsvBuilder<Cohort>(writer)
                    .withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(cohorts);
        }
    }

    /**
     * Reads and returns all {@code Cohort} objects from the CSV file.
     *
     * @return a list of {@code Cohort} objects
     * @throws IOException if an I/O error occurs during reading
     */
    private List<Cohort> readCohortsFromFile() throws IOException {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getCohortsFilepath()))) {
            ColumnPositionMappingStrategy<Cohort> strategy = getMappingStrategy();
            return new CsvToBeanBuilder<Cohort>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        }
    }
}