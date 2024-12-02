package com.projecthub.repository.csv.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.projecthub.config.CsvProperties;
import com.projecthub.model.Student;
import com.projecthub.repository.csv.StudentCsvRepository;
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

@Repository("csvStudentRepository")
public class StudentCsvRepositoryImpl implements StudentCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(StudentCsvRepository.class);

    private final Validator validator;
    private final CsvProperties csvProperties;

    public StudentCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
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
     * Validates a {@link Student} object.
     *
     * @param student the {@code Student} object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateStudent(Student student) {
        Set<ConstraintViolation<Student>> violations = validator.validate(student);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Student> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Student validation failed: " + sb.toString());
        }
    }

    /**
     * Saves a student to the CSV file after validation and backup.
     *
     * @param student the {@code Student} object to save
     * @return the saved {@code Student} object
     * @throws RuntimeException if an error occurs during saving
     */
    @Override
    public Student save(Student student) {
        validateStudent(student);
        try {
            backupCSVFile(csvProperties.getStudentsFilepath());
            List<Student> students = findAll();
            students.removeIf(s -> s.getId().equals(student.getId()));
            students.add(student);
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getStudentsFilepath()))) {
                ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Student.class);
                String[] memberFieldsToBindTo = {"id", "name", "teamId"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Student> beanToCsv = new StatefulBeanToCsvBuilder<Student>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(students);
            }
            logger.info("Student saved successfully: {}", student);
            return student;
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException | com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error saving student to CSV", e);
            throw new RuntimeException("Error saving student to CSV", e);
        }
    }

    /**
     * Retrieves all students from the CSV file.
     *
     * @return a list of {@code Student} objects
     */
    @Override
    public List<Student> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getStudentsFilepath()))) {
            ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Student.class);
            String[] memberFieldsToBindTo = {"id", "name", "teamId"};
            strategy.setColumnMapping(memberFieldsToBindTo);
            return new CsvToBeanBuilder<Student>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            logger.error("Error reading students from CSV", e);
            throw new RuntimeException("Error reading students from CSV", e);
        }
    }

    /**
     * Finds a student by their ID.
     *
     * @param id the ID of the student
     * @return an {@code Optional} containing the student if found, or empty if not found
     */
    @Override
    public Optional<Student> findById(Long id) {
        return findAll().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    /**
     * Deletes a student by their ID.
     *
     * @param id the ID of the student to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(Long id) {
        try {
            backupCSVFile(csvProperties.getStudentsFilepath());
            List<Student> students = findAll();
            students.removeIf(s -> s.getId().equals(id));
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getStudentsFilepath()))) {
                ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Student.class);
                String[] memberFieldsToBindTo = {"id", "name", "teamId"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Student> beanToCsv = new StatefulBeanToCsvBuilder<Student>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(students);
            }
            logger.info("Student deleted successfully: {}", id);
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException | com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting student from CSV", e);
            throw new RuntimeException("Error deleting student from CSV", e);
        }
    }

    /**
     * Finds students by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of {@code Student} objects belonging to the team
     */
    @Override
    public List<Student> findByTeamId(Long teamId) {
        return findAll().stream()
                .filter(s -> s.getTeam().getId().equals(teamId))
                .toList();
    }
}