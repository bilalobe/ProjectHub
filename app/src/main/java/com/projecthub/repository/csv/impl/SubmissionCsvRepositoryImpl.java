package com.projecthub.repository.csv.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.projecthub.config.CsvProperties;
import com.projecthub.model.Submission;
import com.projecthub.repository.csv.SubmissionCsvRepository;

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

@Repository("csvSubmissionRepository")
public class SubmissionCsvRepositoryImpl implements SubmissionCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionCsvRepositoryImpl.class);

    private final Validator validator;
    private final CsvProperties csvProperties;

    public SubmissionCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
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
            throw new IllegalArgumentException("Submission validation failed: " + sb);
        }
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
                ColumnPositionMappingStrategy<Submission> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Submission.class);
                String[] memberFieldsToBindTo = {"id", "projectId", "studentId", "content", "timestamp", "grade"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Submission> beanToCsv = new StatefulBeanToCsvBuilder<Submission>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(submissions);
            }
            logger.info("Submission saved successfully: {}", submission);
            return submission;
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException | com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
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
            ColumnPositionMappingStrategy<Submission> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Submission.class);
            String[] memberFieldsToBindTo = {"id", "projectId", "studentId", "content", "timestamp", "grade"};
            strategy.setColumnMapping(memberFieldsToBindTo);
            return new CsvToBeanBuilder<Submission>(reader)
                    .withMappingStrategy(strategy)
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
    public Optional<Submission> findById(UUID id) {
        return findAll().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    /**
     * Deletes a submission by its ID.
     *
     * @param id the ID of the submission to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(UUID id) {
        try {
            backupCSVFile(csvProperties.getSubmissionsFilepath());
            List<Submission> submissions = findAll();
            submissions.removeIf(s -> s.getId().equals(id));
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getSubmissionsFilepath()))) {
                ColumnPositionMappingStrategy<Submission> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Submission.class);
                String[] memberFieldsToBindTo = {"id", "projectId", "studentId", "content", "timestamp", "grade"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Submission> beanToCsv = new StatefulBeanToCsvBuilder<Submission>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(submissions);
            }
            logger.info("Submission deleted successfully: {}", id);
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException | com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting submission from CSV", e);
            throw new RuntimeException("Error deleting submission from CSV", e);
        }
    }

    /**
     * Finds submissions by student ID.
     *
     * @param studentId the ID of the student
     * @return a list of {@code Submission} objects belonging to the student
     */
    @Override
    public List<Submission> findByStudentId(UUID studentId) {
        return findAll().stream()
                .filter(s -> s.getStudent().getId().equals(studentId))
                .toList();
    }

    /**
     * Finds submissions by project ID.
     *
     * @param projectId the ID of the project
     * @return a list of {@code Submission} objects belonging to the project
     */
    @Override
    public List<Submission> findByProjectId(UUID projectId) {
        return findAll().stream()
                .filter(s -> s.getProject().getId().equals(projectId))
                .toList();
    }
}