package com.projecthub.repository.csv;

import com.projecthub.config.CSVProperties;
import com.projecthub.model.Submission;
import com.projecthub.repository.custom.CustomSubmissionRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * CSV implementation of the {@link CustomSubmissionRepository} interface.
 */
@Repository("csvSubmissionRepository")
public abstract class CSVSubmissionRepository implements CustomSubmissionRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVSubmissionRepository.class);

    private final Validator validator;
    private final CSVProperties csvProperties;


    public CSVSubmissionRepository(CSVProperties csvProperties, Validator validator) {
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
     * Validates a {@link Submission} object.
     *
     * @param submission the {@code Submission} object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateSubmission(Submission submission) {
        Set<ConstraintViolation<Submission>> violations = validator.validate(submission);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Submission> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Submission validation failed: " + sb.toString());
        }
    }

    /**
     * Defines the column mapping strategy for {@link Submission}.
     *
     * @return the configured {@link ColumnPositionMappingStrategy}
     */
    private ColumnPositionMappingStrategy<Submission> getColumnMappingStrategy() {
        ColumnPositionMappingStrategy<Submission> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Submission.class);
        String[] memberFieldsToBindTo = {"id", "content", "timestamp", "studentId", "projectId", "grade"};
        strategy.setColumnMapping(memberFieldsToBindTo);
        return strategy;
    }

    /**
     * Saves a submission to the CSV file after validation and backup.
     *
     * @param submission the {@code Submission} object to save
     * @return the saved {@code Submission} object
     * @throws RuntimeException if an error occurs during saving
     */
    @Override
    public Submission save(Submission submission) {
        validateSubmission(submission);
        try {
            backupCSVFile(csvProperties.getSubmissionsFilepath());
            List<Submission> submissions = findAll();
            submissions.removeIf(s -> s.getId().equals(submission.getId()));
            submissions.add(submission);

            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getSubmissionsFilepath()))) {
                StatefulBeanToCsv<Submission> beanToCsv = new StatefulBeanToCsvBuilder<Submission>(writer)
                        .withMappingStrategy(getColumnMappingStrategy())
                        .build();
                beanToCsv.write(submissions);
            }

            logger.info("Submission saved successfully: {}", submission);
            return submission;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error saving submission to CSV", e);
            throw new RuntimeException("Error saving submission to CSV", e);
        }
    }

    /**
     * Retrieves all submissions from the CSV file.
     *
     * @return a list of {@code Submission} objects
     */
    @Override
    public List<Submission> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getSubmissionsFilepath()))) {
            return new CsvToBeanBuilder<Submission>(reader)
                    .withMappingStrategy(getColumnMappingStrategy())
                    .build()
                    .parse();
        } catch (IOException e) {
            logger.error("Error reading submissions from CSV", e);
            throw new RuntimeException("Error reading submissions from CSV", e);
        }
    }

    /**
     * Finds a submission by its ID.
     *
     * @param id the ID of the submission
     * @return an {@code Optional} containing the submission if found, or empty if not found
     */
    @Override
    public Optional<Submission> findById(Long id) {
        return findAll().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    /**
     * Deletes a submission by its ID.
     *
     * @param submissionId the ID of the submission to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    public void deleteById(Long submissionId) {
        try {
            backupCSVFile(csvProperties.getSubmissionsFilepath());
            List<Submission> submissions = findAll();
            submissions.removeIf(submission -> submission.getId().equals(submissionId));

            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getSubmissionsFilepath()))) {
                StatefulBeanToCsv<Submission> beanToCsv = new StatefulBeanToCsvBuilder<Submission>(writer)
                        .withMappingStrategy(getColumnMappingStrategy())
                        .build();
                beanToCsv.write(submissions);
            }

            logger.info("Submission deleted successfully: {}", submissionId);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting submission from CSV", e);
            throw new RuntimeException("Error deleting submission from CSV", e);
        }
    }
}