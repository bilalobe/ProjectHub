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
import com.projecthub.model.User;
import com.projecthub.repository.custom.CustomUserRepository;

@Repository("csvUserRepository")
public abstract class CSVUserRepository implements CustomUserRepository {

    @Value("${app.users.filepath}")
    private String usersFilePath;

    public User save(User user) {
        try {
            List<User> users = findAll();
            users.removeIf(u -> u.getId().equals(user.getId()));
            users.add(user);

            try (CSVWriter writer = new CSVWriter(new FileWriter(usersFilePath))) {
                ColumnPositionMappingStrategy<User> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(User.class);
                String[] memberFieldsToBindTo = {"id", "username", "password"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<User> beanToCsv = new StatefulBeanToCsvBuilder<User>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(users);
            }

            return user;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Error saving user to CSV", e);
        }
    }

    @Override
    public List<User> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(usersFilePath))) {
            ColumnPositionMappingStrategy<User> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(User.class);
            String[] memberFieldsToBindTo = {"id", "username", "password"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            return new CsvToBeanBuilder<User>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException("Error reading users from CSV", e);
        }
    }

    @Override
    public void deleteById(Long userId) {
        try {
            List<User> users = findAll();
            users.removeIf(user -> user.getId().equals(userId));

            try (CSVWriter writer = new CSVWriter(new FileWriter(usersFilePath))) {
                ColumnPositionMappingStrategy<User> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(User.class);
                String[] memberFieldsToBindTo = {"id", "username", "password"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<User> beanToCsv = new StatefulBeanToCsvBuilder<User>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(users);
            }
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Error deleting user from CSV", e);
        }
    }
}