package com.projecthub.repository.csv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
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
import com.projecthub.model.Student;
import com.projecthub.repository.custom.CustomStudentRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * CSV implementation of the CustomStudentRepository interface.
 */
@Repository("csvStudentRepository")
public abstract class CSVStudentRepository implements CustomStudentRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVStudentRepository.class);
    private final Validator validator;

    @Value("${app.students.filepath}")
    private String studentsFilePath;

    public CSVStudentRepository() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private void backupCSVFile(String filePath) throws IOException {
        Path source = Path.of(filePath);
        Path backup = Path.of(filePath + ".backup");
        Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created for file: {}", filePath);
    }

    /**
     * Saves a student to the CSV file after validation and backup.
     *
     * @param student the Student object to save
     * @return the saved Student object
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
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error saving student to CSV", e);
            throw new RuntimeException("Error saving student to CSV", e);
        }
    }

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

    @Override
    public List<Student> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(studentsFilePath))) {
            ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Student.class);
            String[] memberFieldsToBindTo = {"id", "name"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<Student> students = new CsvToBeanBuilder<Student>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();

            logger.info("Students retrieved successfully");
            return students;
        } catch (IOException e) {
            logger.error("Error reading students from CSV", e);
            throw new RuntimeException("Error reading students from CSV", e);
        }
    }

    /**
     * Deletes a student by their ID.
     *
     * @param studentId the ID of the student to delete
     */
    @Override
    public void deleteById(Long studentId) {
        try {
            backupCSVFile(studentsFilePath);
            List<Student> students = findAll();
            students.removeIf(student -> student.getId().equals(studentId));

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

            logger.info("Student deleted successfully: {}", studentId);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting student from CSV", e);
            throw new RuntimeException("Error deleting student from CSV", e);
        }
    }

    @Override
    public Optional<Student> findById(Long id) {
        return findAll().stream()
                .filter(student -> student.getId().equals(id))
                .findFirst();
    }
}