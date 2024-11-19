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
import com.projecthub.model.Student;
import com.projecthub.repository.custom.CustomStudentRepository;

@Repository("csvStudentRepository")
public abstract class CSVStudentRepository implements CustomStudentRepository {

    @Value("${app.students.filepath}")
    private String studentsFilePath;

    @Override
    public Student save(Student student) {
        try {
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

            return student;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Error saving student to CSV", e);
        }
    }

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
            throw new RuntimeException("Error reading students from CSV", e);
        }
    }

    @Override
    public void deleteById(Long studentId) {
        try {
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
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Error deleting student from CSV", e);
        }
    }
}