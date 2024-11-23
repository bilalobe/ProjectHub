package com.projecthub.repository.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.*;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;
import com.projecthub.repository.custom.CustomCohortRepository;
import com.projecthub.repository.custom.CustomSchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.*;
import java.nio.file.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CSV implementation of the CustomCohortRepository interface.
 */
@Repository("csvCohortRepository")
public class CSVCohortRepository implements CustomCohortRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVCohortRepository.class);
    private final Validator validator;

    @Value("${app.cohorts.filepath}")
    private String cohortsFilePath;

    @Autowired
    private CustomSchoolRepository schoolRepository;

    public CSVCohortRepository() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private void backupCSVFile(String filePath) throws IOException {
        java.nio.file.Path source = java.nio.file.Path.of(filePath);
        java.nio.file.Path backup = java.nio.file.Path.of(filePath + ".backup");
        Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created for file: {}", filePath);
    }

    private void validateCohort(Cohort cohort) {
        Set<ConstraintViolation<Cohort>> violations = validator.validate(cohort);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Cohort> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Cohort validation failed: " + sb.toString());
        }
    }

    /**
     * Saves a cohort to the CSV file after validation and backup.
     *
     * @param cohort the Cohort object to save
     * @return the saved Cohort object
     */
    @Override
    public Cohort save(Cohort cohort) {
        validateCohort(cohort);
        try {
            backupCSVFile(cohortsFilePath);
            List<Cohort> cohorts = findAll();
            cohorts.removeIf(c -> c.getId().equals(cohort.getId()));

            // Assign an ID if it's null
            if (cohort.getId() == null) {
                Long maxId = cohorts.stream()
                        .map(Cohort::getId)
                        .max(Long::compareTo)
                        .orElse(0L);
                cohort.setId(maxId + 1);
            }

            cohorts.add(cohort);

            try (CSVWriter writer = new CSVWriter(new FileWriter(cohortsFilePath))) {
                ColumnPositionMappingStrategy<CohortCSV> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(CohortCSV.class);

                String[] memberFieldsToBindTo = {"id", "name", "schoolId"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                List<CohortCSV> csvCohorts = cohorts.stream()
                        .map(CohortCSV::fromCohort)
                        .collect(Collectors.toList());

                StatefulBeanToCsv<CohortCSV> beanToCsv = new StatefulBeanToCsvBuilder<CohortCSV>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(csvCohorts);
            }

            logger.info("Cohort saved successfully: {}", cohort);
            return cohort;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error saving cohort to CSV", e);
            throw new RuntimeException("Error saving cohort to CSV", e);
        }
    }

    @Override
    public List<Cohort> findAll() {
        File file = new File(cohortsFilePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (CSVReader reader = new CSVReader(new FileReader(cohortsFilePath))) {
            ColumnPositionMappingStrategy<CohortCSV> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(CohortCSV.class);

            String[] memberFieldsToBindTo = {"id", "name", "schoolId"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<CohortCSV> csvCohorts = new CsvToBeanBuilder<CohortCSV>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();

            return csvCohorts.stream()
                    .map(this::toCohort)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error reading cohorts from CSV", e);
        }
    }

    @Override
    public Optional<Cohort> findById(Long id) {
        return findAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    /**
     * Deletes a cohort by its ID.
     *
     * @param id the ID of the cohort to delete
     */
    @Override
    public void deleteById(Long id) {
        try {
            backupCSVFile(cohortsFilePath);
            List<Cohort> cohorts = findAll();
            cohorts.removeIf(c -> c.getId().equals(id));

            try (CSVWriter writer = new CSVWriter(new FileWriter(cohortsFilePath))) {
                ColumnPositionMappingStrategy<CohortCSV> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(CohortCSV.class);

                String[] memberFieldsToBindTo = {"id", "name", "schoolId"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                List<CohortCSV> csvCohorts = cohorts.stream()
                        .map(CohortCSV::fromCohort)
                        .collect(Collectors.toList());

                StatefulBeanToCsv<CohortCSV> beanToCsv = new StatefulBeanToCsvBuilder<CohortCSV>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(csvCohorts);
            }

            logger.info("Cohort deleted successfully: {}", id);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting cohort from CSV", e);
            throw new RuntimeException("Error deleting cohort from CSV", e);
        }
    }

    /**
     * Finds all cohorts associated with a specific school ID.
     *
     * @param schoolId the ID of the school
     * @return a list of cohorts belonging to the school
     */
    @Override
    public List<Cohort> findBySchoolId(Long schoolId) {
        return findAll().stream()
                .filter(c -> c.getSchool() != null && c.getSchool().getId().equals(schoolId))
                .collect(Collectors.toList());
    }

    private Cohort toCohort(CohortCSV csvCohort) {
        Cohort cohort = new Cohort();
        cohort.setId(csvCohort.getId());
        cohort.setName(csvCohort.getName());

        if (csvCohort.getSchoolId() != null) {
            Optional<School> school = schoolRepository.findById(csvCohort.getSchoolId());
            school.ifPresent(cohort::setSchool);
        }

        return cohort;
    }

    /**
     * Inner class for CSV mapping.
     */
    public static class CohortCSV {

        @CsvBindByName(column = "id")
        private Long id;

        @CsvBindByName(column = "name")
        private String name;

        @CsvBindByName(column = "schoolId")
        private Long schoolId;

        // Getters and Setters

        public static CohortCSV fromCohort(Cohort cohort) {
            CohortCSV csv = new CohortCSV();
            csv.setId(cohort.getId());
            csv.setName(cohort.getName());
            csv.setSchoolId(cohort.getSchool() != null ? cohort.getSchool().getId() : null);
            return csv;
        }

        // Getters and setters...
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(Long schoolId) {
            this.schoolId = schoolId;
        }
    }
}