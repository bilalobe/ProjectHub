package com.projecthub.repository.csv;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.projecthub.config.CSVProperties;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;
import com.projecthub.repository.custom.CustomCohortRepository;
import com.projecthub.repository.custom.CustomSchoolRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * CSV implementation of the {@link CustomCohortRepository} interface.
 */
@Repository("csvCohortRepository")
public class CSVCohortRepository implements CustomCohortRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVCohortRepository.class);
    private final Validator validator;
    private final CustomSchoolRepository schoolRepository;
    private final CSVProperties csvProperties;

    public CSVCohortRepository(CustomSchoolRepository schoolRepository, CSVProperties csvProperties) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        this.schoolRepository = schoolRepository;
        this.csvProperties = csvProperties;
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

    @Override
    public Cohort save(Cohort cohort) {
        validateCohort(cohort);
        try {
            backupCSVFile(csvProperties.getCohortsFilepath());
            List<Cohort> cohorts = findAll();
            cohorts.removeIf(c -> c.getId().equals(cohort.getId()));

            if (cohort.getId() == null) {
                Long maxId = cohorts.stream()
                        .map(Cohort::getId)
                        .max(Long::compareTo)
                        .orElse(0L);
                cohort.setId(maxId + 1);
            }

            cohorts.add(cohort);

            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getCohortsFilepath()))) {
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
        File file = new File(csvProperties.getCohortsFilepath());
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getCohortsFilepath()))) {
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

    @Override
    public void deleteById(Long id) {
        try {
            backupCSVFile(csvProperties.getCohortsFilepath());
            List<Cohort> cohorts = findAll();
            cohorts.removeIf(c -> c.getId().equals(id));

            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getCohortsFilepath()))) {
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

    public static class CohortCSV {

        @CsvBindByName(column = "id")
        private Long id;

        @CsvBindByName(column = "name")
        private String name;

        @CsvBindByName(column = "schoolId")
        private Long schoolId;

        public static CohortCSV fromCohort(Cohort cohort) {
            CohortCSV csv = new CohortCSV();
            csv.setId(cohort.getId());
            csv.setName(cohort.getName());
            csv.setSchoolId(cohort.getSchool() != null ? cohort.getSchool().getId() : null);
            return csv;
        }

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