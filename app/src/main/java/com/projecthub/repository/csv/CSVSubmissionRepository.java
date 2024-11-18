package com.projecthub.repository.csv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

@Repository("csvSubmissionRepository")
public abstract class CSVSubmissionRepository implements CustomSubmissionRepository {

    @Value("${app.submissions.filepath}")
    private String submissionsFilePath;

    @Override
    public Submission save(Submission submission) {
        try {
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

            return submission;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
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
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Error deleting submission from CSV", e);
        }
    }
}