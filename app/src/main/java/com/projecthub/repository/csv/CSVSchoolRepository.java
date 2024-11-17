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
import com.projecthub.model.School;
import com.projecthub.repository.custom.CustomSchoolRepository;

@Repository("csvSchoolRepository")
public abstract class CSVSchoolRepository implements CustomSchoolRepository {

    @Value("${app.schools.filepath}")
    private String schoolsFilePath;

    @Override
    public School save(School school) {
        try {
            List<School> schools = findAll();
            schools.removeIf(s -> s.getId().equals(school.getId()));
            schools.add(school);

            try (CSVWriter writer = new CSVWriter(new FileWriter(schoolsFilePath))) {
                ColumnPositionMappingStrategy<School> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(School.class);
                String[] memberFieldsToBindTo = {"id", "name"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<School> beanToCsv = new StatefulBeanToCsvBuilder<School>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(schools);
            }

            return school;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Error saving school to CSV", e);
        }
    }

    @Override
    public List<School> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(schoolsFilePath))) {
            ColumnPositionMappingStrategy<School> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(School.class);
            String[] memberFieldsToBindTo = {"id", "name"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            return new CsvToBeanBuilder<School>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException("Error reading schools from CSV", e);
        }
    }

    @Override
    public void deleteById(Long schoolId) {
        try {
            List<School> schools = findAll();
            schools.removeIf(school -> school.getId().equals(schoolId));

            try (CSVWriter writer = new CSVWriter(new FileWriter(schoolsFilePath))) {
                ColumnPositionMappingStrategy<School> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(School.class);
                String[] memberFieldsToBindTo = {"id", "name"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<School> beanToCsv = new StatefulBeanToCsvBuilder<School>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(schools);
            }
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Error deleting school from CSV", e);
        }
    }
}