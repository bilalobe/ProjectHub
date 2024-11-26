package com.projecthub.repository.csv;

import com.projecthub.model.Student;
import com.projecthub.repository.custom.CustomStudentRepository;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CSV implementation of the {@link CustomStudentRepository} interface.
 */
@Repository("csvStudentRepository")
public abstract class CSVStudentRepository implements CustomStudentRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVStudentRepository.class);

    @Value("${app.students.filepath}")
    private String studentsFilePath;

    private final Validator validator;

    /**
     * Constructs a new {@code CSVStudentRepository}.
     *
     * @param validator the {@code Validator} instance for validating student entities
     */
    public CSVStudentRepository(Validator validator) {
        this.validator = validator;
    }

    /**
     * Creates a backup of the CSV file.
     *
     * @param filePath the path of the CSV file to back up
     * @throws IOException if an I/O error occurs during backup
     */
    private void backupCSVFile(String filePath) throws IOException {
        java.nio.file.Path source = java.nio.file.Path.of(filePath);
        java.nio.file.Path backup = java.nio.file.Path.of(filePath + ".backup");
        java.nio.file.Files.copy(source, backup, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
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
            backupCSVFile(studentsFilePath);
            List<Student> students = findAll();
            students.removeIf(s -> s.getId().equals(student.getId()));
            students.add(student);
            try (CSVWriter writer = new CSVWriter(new FileWriter(studentsFilePath))) {
                ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Student.class);
                String[] memberFieldsToBindTo = {"id", "name"};
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
        try (CSVReader reader = new CSVReader(new FileReader(studentsFilePath))) {
            ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Student.class);
            String[] memberFieldsToBindTo = {"id", "name"};
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
            backupCSVFile(studentsFilePath);
            List<Student> students = findAll();
            students.removeIf(s -> s.getId().equals(id));
            try (CSVWriter writer = new CSVWriter(new FileWriter(studentsFilePath))) {
                ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Student.class);
                String[] memberFieldsToBindTo = {"id", "name"};
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
}