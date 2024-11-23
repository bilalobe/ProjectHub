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
import com.projecthub.model.Component;
import com.projecthub.repository.custom.CustomComponentRepository;

@Repository("csvComponentRepository")
public abstract class CSVComponentRepository implements CustomComponentRepository {

    @Value("${app.components.filepath}")
    private String componentsFilePath;

    public Component save(Component component) {
        try {
            List<Component> components = findAll();
            components.removeIf(c -> c.getId().equals(component.getId()));
            components.add(component);

            try (CSVWriter writer = new CSVWriter(new FileWriter(componentsFilePath))) {
                ColumnPositionMappingStrategy<Component> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Component.class);
                String[] memberFieldsToBindTo = {"id", "name", "description", "projectId"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<Component> beanToCsv = new StatefulBeanToCsvBuilder<Component>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(components);
            }

            return component;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Error saving component to CSV", e);
        }
    }

    @Override
    public List<Component> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(componentsFilePath))) {
            ColumnPositionMappingStrategy<Component> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Component.class);
            String[] memberFieldsToBindTo = {"id", "name", "description", "projectId"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            return new CsvToBeanBuilder<Component>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException("Error reading components from CSV", e);
        }
    }

    @Override
    public void deleteById(Long componentId) {
        try {
            List<Component> components = findAll();
            components.removeIf(component -> component.getId().equals(componentId));

            try (CSVWriter writer = new CSVWriter(new FileWriter(componentsFilePath))) {
                ColumnPositionMappingStrategy<Component> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Component.class);
                String[] memberFieldsToBindTo = {"id", "name", "description", "projectId"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<Component> beanToCsv = new StatefulBeanToCsvBuilder<Component>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(components);
            }
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Error deleting component from CSV", e);
        }
    }
}