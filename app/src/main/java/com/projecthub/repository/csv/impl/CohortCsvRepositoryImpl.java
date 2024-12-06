package com.projecthub.repository.csv.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.projecthub.config.CsvProperties;
import com.projecthub.model.Cohort;
import com.projecthub.repository.csv.CohortCsvRepository;
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
import java.util.UUID;

@Repository("csvCohortRepository")
public class CohortCsvRepositoryImpl implements CohortCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(CohortCsvRepositoryImpl.class);

    private final CsvProperties csvProperties;
    private final Validator validator;

    public CohortCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
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
     * Validates a {@link Cohort} object.
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
     * Saves a cohort to the CSV file after validation and backup.
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
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getCohortsFilepath()))) {
                ColumnPositionMappingStrategy<Cohort> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Cohort.class);
                String[] memberFieldsToBindTo = {"id", "name", "schoolId"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Cohort> beanToCsv = new StatefulBeanToCsvBuilder<Cohort>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(cohorts);
            }
            logger.info("Cohort saved successfully: {}", cohort);
            return cohort;
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException | com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error saving cohort to CSV", e);
            throw new RuntimeException("Error saving cohort to CSV", e);
        }
    }

    /**
     * Retrieves all cohorts from the CSV file.
     *
     * @return a list of {@code Cohort} objects
     */
    @Override
    public List<Cohort> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getCohortsFilepath()))) {
            ColumnPositionMappingStrategy<Cohort> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Cohort.class);
            String[] memberFieldsToBindTo = {"id", "name", "schoolId"};
            strategy.setColumnMapping(memberFieldsToBindTo);
            return new CsvToBeanBuilder<Cohort>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            logger.error("Error reading cohorts from CSV", e);
            throw new RuntimeException("Error reading cohorts from CSV", e);
        }
    }

    /**
     * Finds a cohort by their ID.
     *
     * @param id the ID of the cohort
     * @return an {@code Optional} containing the cohort if found, or empty if not found
     */
    @Override
    public Optional<Cohort> findById(UUID id) {
        return findAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    /**
     * Deletes a cohort by their ID.
     *
     * @param id the ID of the cohort to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(UUID id) {
        try {
            backupCSVFile(csvProperties.getCohortsFilepath());
            List<Cohort> cohorts = findAll();
            cohorts.removeIf(c -> c.getId().equals(id));
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getCohortsFilepath()))) {
                ColumnPositionMappingStrategy<Cohort> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Cohort.class);
                String[] memberFieldsToBindTo = {"id", "name", "schoolId"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Cohort> beanToCsv = new StatefulBeanToCsvBuilder<Cohort>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(cohorts);
            }
            logger.info("Cohort deleted successfully: {}", id);
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException | com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting cohort from CSV", e);
            throw new RuntimeException("Error deleting cohort from CSV", e);
        }
    }

    /**
     * Finds cohorts by their school ID.
     *
     * @param schoolId the ID of the school
     * @return a list of {@code Cohort} objects belonging to the school
     */
    @Override
    public List<Cohort> findBySchoolId(UUID schoolId) {
        return findAll().stream()
                .filter(c -> c.getSchool().getId().equals(schoolId))
                .toList();
    }
}