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

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.projecthub.model.Submission;
import com.projecthub.repository.custom.CustomSubmissionRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Repository("csvSubmissionRepository")
public abstract class CSVSubmissionRepository implements CustomSubmissionRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVSubmissionRepository.class);
    private final Validator validator;

    @Value("${app.submissions.filepath}")
    private String submissionsFilePath;

    public CSVSubmissionRepository() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private void backupCSVFile(String filePath) throws IOException {
        Path source = Path.of(filePath);
        Path backup = Path.of(filePath + ".backup");
        Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created for file: {}", filePath);
    }

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

    @Override
    public Submission save(Submission submission) {
        validateSubmission(submission);
        try {
            backupCSVFile(submissionsFilePath);
            List<Submission> submissions = findAll();
            submissions.removeIf(s -> s.getId().equals(submission.getId()));
            submissions.add(submission);

            try (CSVWriter writer = new CSVWriter(new FileWriter(submissionsFilePath))) {
                ColumnPositionMappingStrategy<Submission> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Submission.class);
                String[] memberFieldsToBindTo = {"id", "content", "timestamp", "studentId", "projectId", "grade"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<Submission> beanToCsv = new StatefulBeanToCsvBuilder<Submission>(writer)
                        .withMappingStrategy(strategy)
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

    @Override
    public List<Submission> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(submissionsFilePath))) {
            ColumnPositionMappingStrategy<Submission> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Submission.class);
            String[] memberFieldsToBindTo = {"id", "content", "timestamp", "studentId", "projectId", "grade"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            return new CsvToBeanBuilder<Submission>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException("Error reading submissions from CSV", e);
        }
    }

    @Override
    public void deleteById(Long submissionId) {
        try {
            backupCSVFile(submissionsFilePath);
            List<Submission> submissions = findAll();
            submissions.removeIf(submission -> submission.getId().equals(submissionId));

            try (CSVWriter writer = new CSVWriter(new FileWriter(submissionsFilePath))) {
                ColumnPositionMappingStrategy<Submission> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Submission.class);
                String[] memberFieldsToBindTo = {"id", "content", "timestamp", "studentId", "projectId", "grade"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<Submission> beanToCsv = new StatefulBeanToCsvBuilder<Submission>(writer)
                        .withMappingStrategy(strategy)
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