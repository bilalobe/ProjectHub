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
import org.springframework.lang.NonNull;
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
import com.projecthub.model.School;
import com.projecthub.repository.custom.CustomSchoolRepository;

/**
 * CSV implementation of the CustomSchoolRepository interface.
 */
@Repository("csvSchoolRepository")
public abstract class CSVSchoolRepository implements CustomSchoolRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVSchoolRepository.class);
    private final Validator validator;

    @Value("${app.schools.filepath}")
    private String schoolsFilePath;

    public CSVSchoolRepository() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private void backupCSVFile(String filePath) throws IOException {
        Path source = Path.of(filePath);
        Path backup = Path.of(filePath + ".backup");
        Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created for file: {}", filePath);
    }

    private void validateSchool(School school) {
        Set<ConstraintViolation<School>> violations = validator.validate(school);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<School> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("School validation failed: " + sb.toString());
        }
    }

    /**
     * Saves a school to the CSV file after validation and backup.
     *
     * @param school the School object to save
     * @return the saved School object
     */
    
    @NonNull
    @Override
    public <S extends School> S save(@NonNull S school) {
        validateSchool(school);
        try {
            backupCSVFile(schoolsFilePath);
            List<School> schools = findAll();
            schools.removeIf(s -> s.getId().equals(school.getId()));
            schools.add(school);

            try (CSVWriter writer = new CSVWriter(new FileWriter(schoolsFilePath))) {
                ColumnPositionMappingStrategy<School> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(School.class);
                String[] memberFieldsToBindTo = {"id", "name"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<School> beanToCsv = new StatefulBeanToCsvBuilder<School>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(schools);
            }
            logger.info("School saved successfully: {}", school);
            return school;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error saving school to CSV", e);
            throw new RuntimeException("Error saving school to CSV", e);
        }
    }

    @Override
    @NonNull
    public List<School> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(schoolsFilePath))) {
            ColumnPositionMappingStrategy<School> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(School.class);
            String[] memberFieldsToBindTo = {"id", "name"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            return new CsvToBeanBuilder<School>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException("Error reading schools from CSV", e);
        }
    }

    /**
     * Deletes a school by its ID.
     *
     * @param schoolId the ID of the school to delete
     */
    @Override
    public void deleteById(@NonNull Long schoolId) {
        try {
            backupCSVFile(schoolsFilePath);
            List<School> schools = findAll();
            schools.removeIf(school -> school.getId().equals(schoolId));

            try (CSVWriter writer = new CSVWriter(new FileWriter(schoolsFilePath))) {
                ColumnPositionMappingStrategy<School> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(School.class);
                String[] memberFieldsToBindTo = {"id", "name"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<School> beanToCsv = new StatefulBeanToCsvBuilder<School>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(schools);
            }
            logger.info("School deleted successfully: {}", schoolId);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting school from CSV", e);
            throw new RuntimeException("Error deleting school from CSV", e);
        }
    }
}