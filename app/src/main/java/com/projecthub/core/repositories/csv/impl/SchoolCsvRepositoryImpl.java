package com.projecthub.core.repositories.csv.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.projecthub.config.CsvProperties;
import com.projecthub.core.models.School;
import com.projecthub.core.repositories.csv.SchoolCsvRepository;
import com.projecthub.core.repositories.csv.helper.CsvHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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
import java.util.Objects;

@Repository("csvSchoolRepository")
@Profile("csv")
public class SchoolCsvRepositoryImpl implements SchoolCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(SchoolCsvRepositoryImpl.class);

    private final Validator validator;
    private final CsvProperties csvProperties;

    public SchoolCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
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
     * Validates a {@link School} object.
     *
     * @param school the {@code School} object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateSchool(School school) {
        Set<ConstraintViolation<School>> violations = validator.validate(school);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<School> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("School validation failed: " + sb);
        }
    }

    /**
     * Saves a school to the CSV file after validation and backup.
     *
     * @param school the {@code School} object to save
     * @return the saved {@code School} object
     * @throws RuntimeException if an error occurs during saving
     */
    @Override
    public School save(School school) {
        validateSchool(school);
        try {
            backupCSVFile(csvProperties.getSchoolsFilepath());
            List<School> schools = findAll();
            schools.removeIf(s -> Objects.equals(s.getId(), school.getId()));
            schools.add(school);
            String[] columns = {"id", "name"};
            CsvHelper.writeBeansToCsv(csvProperties.getSchoolsFilepath(), School.class, schools, columns);
            logger.info("School saved successfully: {}", school);
            return school;
        } catch (Exception e) {
            logger.error("Error saving school to CSV", e);
            throw new RuntimeException("Error saving school to CSV", e);
        }
    }

    /**
     * Retrieves all schools from the CSV file.
     *
     * @return a list of {@code School} objects
     */
    @Override
    public List<School> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getSchoolsFilepath()))) {
            ColumnPositionMappingStrategy<School> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(School.class);
            String[] memberFieldsToBindTo = {"id", "name"};
            strategy.setColumnMapping(memberFieldsToBindTo);
            return new CsvToBeanBuilder<School>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            logger.error("Error reading schools from CSV", e);
            throw new RuntimeException("Error reading schools from CSV", e);
        }
    }

    /**
     * Finds a school by its ID.
     *
     * @param id the ID of the school
     * @return an {@code Optional} containing the school if found, or empty if not found
     */
    @Override
    public Optional<School> findById(UUID id) {
        return findAll().stream()
                .filter(s -> Objects.equals(s.getId(), id))
                .findFirst();
    }

    /**
     * Deletes a school by its ID.
     *
     * @param id the ID of the school to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(UUID id) {
        try {
            backupCSVFile(csvProperties.getSchoolsFilepath());
            List<School> schools = findAll();
            schools.removeIf(s -> Objects.equals(s.getId(), id));
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getSchoolsFilepath()))) {
                ColumnPositionMappingStrategy<School> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(School.class);
                String[] memberFieldsToBindTo = {"id", "name"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<School> beanToCsv = new StatefulBeanToCsvBuilder<School>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(schools);
            }
            logger.info("School deleted successfully: {}", id);
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException |
                 com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting school from CSV", e);
            throw new RuntimeException("Error deleting school from CSV", e);
        }
    }
}