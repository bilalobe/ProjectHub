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
import org.springframework.stereotype.Repository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.projecthub.config.CSVProperties;
import com.projecthub.model.AppUser;
import com.projecthub.repository.custom.CustomUserRepository;

/**
 * CSV implementation of the CustomUserRepository interface.
 */
@Repository("csvUserRepository")
public abstract class CSVUserRepository implements CustomUserRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVUserRepository.class);
    private final Validator validator;
    private final CSVProperties csvProperties;


    public CSVUserRepository(CSVProperties csvProperties) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        this.csvProperties = csvProperties;
    }

    private void backupCSVFile(String filePath) throws IOException {
        Path source = Path.of(filePath);
        Path backup = Path.of(filePath + ".backup");
        Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created for file: {}", filePath);
    }

    private void validateUser(AppUser user) {
        Set<ConstraintViolation<AppUser>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<AppUser> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("User validation failed: " + sb.toString());
        }
    }

    @Override
    public AppUser save(AppUser user) {
        validateUser(user);
        try {
            backupCSVFile(csvProperties.getUsersFilepath());
            List<AppUser> users = findAll();
            users.removeIf(u -> u.getId().equals(user.getId()));
            users.add(user);

            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getUsersFilepath()))) {
                ColumnPositionMappingStrategy<AppUser> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(AppUser.class);
                String[] memberFieldsToBindTo = {"id", "username", "password", "firstName", "lastName", "email", "team"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<AppUser> beanToCsv = new StatefulBeanToCsvBuilder<AppUser>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(users);
            }
            logger.info("User saved successfully: {}", user);
            return user;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error saving user to CSV", e);
            throw new RuntimeException("Error saving user to CSV", e);
        }
    }

    @Override
    public List<AppUser> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getUsersFilepath()))) {
            ColumnPositionMappingStrategy<AppUser> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(AppUser.class);
            String[] memberFieldsToBindTo = {"id", "username", "password", "firstName", "lastName", "email", "team"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            return new CsvToBeanBuilder<AppUser>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException("Error reading users from CSV", e);
        }
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        return findAll().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public void deleteById(Long userId) {
        try {
            backupCSVFile(csvProperties.getUsersFilepath());
            List<AppUser> users = findAll();
            users.removeIf(user -> user.getId().equals(userId));

            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getUsersFilepath()))) {
                ColumnPositionMappingStrategy<AppUser> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(AppUser.class);
                String[] memberFieldsToBindTo = {"id", "username", "password", "firstName", "lastName", "email", "team"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<AppUser> beanToCsv = new StatefulBeanToCsvBuilder<AppUser>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(users);
            }
            logger.info("User deleted successfully: {}", userId);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting user from CSV", e);
            throw new RuntimeException("Error deleting user from CSV", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }
}