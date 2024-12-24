package projecthub.csv.plugin.impl;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.projecthub.core.models.AppUser;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import projecthub.csv.plugin.config.CsvProperties;
import projecthub.csv.plugin.helper.CsvFileHelper;

@Repository("csvUserRepository")
@Profile("csv")
public class AppUserCsvRepositoryImpl implements AppUserCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(AppUserCsvRepositoryImpl.class);
    private static final String[] COLUMNS = {"id", "username", "email", "firstName", "lastName", "teamId"};
    private final Validator validator;
    private final CsvProperties csvProperties;

    public AppUserCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
        this.csvProperties = csvProperties;
        this.validator = validator;
    }

    /**
     * Validates an {@link AppUser} object.
     *
     * @param user the {@code AppUser} object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateUser(AppUser user) {
        Set<ConstraintViolation<AppUser>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<AppUser> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("User validation failed: " + sb);
        }
    }

    /**
     * Saves a user to the CSV file after validation and backup.
     *
     * @param user the {@code AppUser} object to save
     * @return the saved {@code AppUser} object
     * @throws CsvDataAccessException if an error occurs during saving
     */
    public AppUser save(AppUser user) {
        validateUser(user);
        try {
            CsvFileHelper.backupCSVFile(csvProperties.getUsersFilepath());
            List<AppUser> users = findAll();
            users.removeIf(u -> Objects.equals(u.getId(), user.getId()));
            users.add(user);
            CsvHelper.writeBeansToCsv(csvProperties.getUsersFilepath(), AppUser.class, users, COLUMNS);
            logger.info("User saved successfully: {}", user);
            return user;
        } catch (Exception e) {
            logger.error("Error saving user to CSV", e);
            throw new CsvDataAccessException("Error saving user to CSV", e);
        }
    }

    /**
     * Retrieves all users from the CSV file.
     *
     * @return a list of {@code AppUser} objects
     * @throws CsvDataAccessException if an error occurs during reading
     */
    @Override
    public List<AppUser> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getUsersFilepath()))) {
            ColumnPositionMappingStrategy<AppUser> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(AppUser.class);
            strategy.setColumnMapping(COLUMNS);
            return new CsvToBeanBuilder<AppUser>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            logger.error("Error reading users from CSV", e);
            throw new CsvDataAccessException("Error reading users from CSV", e);
        }
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user
     * @return an {@code Optional} containing the user if found, or empty if not found
     */
    @Override
    public Optional<AppUser> findById(UUID id) {
        return findAll().stream()
                .filter(u -> Objects.equals(u.getId(), id))
                .findFirst();
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @throws CsvDataAccessException if an error occurs during deletion
     */
    public void deleteById(UUID id) {
        try {
            CsvFileHelper.backupCSVFile(csvProperties.getUsersFilepath());
            List<AppUser> users = findAll();
            users.removeIf(u -> Objects.equals(u.getId(), id));
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getUsersFilepath()))) {
                ColumnPositionMappingStrategy<AppUser> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(AppUser.class);
                strategy.setColumnMapping(COLUMNS);
                StatefulBeanToCsv<AppUser> beanToCsv = new StatefulBeanToCsvBuilder<AppUser>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(users);
            }
            logger.info("User deleted successfully: {}", id);
        } catch (IOException e) {
            logger.error("IO Error deleting user from CSV", e);
            throw new CsvDataAccessException("IO Error deleting user from CSV", e);
        } catch (com.opencsv.exceptions.CsvDataTypeMismatchException e) {
            logger.error("Data type mismatch error deleting user from CSV", e);
            throw new CsvDataAccessException("Data type mismatch error deleting user from CSV", e);
        } catch (com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Required field empty error deleting user from CSV", e);
            throw new CsvDataAccessException("Required field empty error deleting user from CSV", e);
        }
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return an {@code Optional} containing the user if found, or empty if not found
     */
    @Override
    public Optional<AppUser> findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }
}