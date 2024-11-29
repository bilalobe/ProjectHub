package com.projecthub.repository.csv;

import com.projecthub.model.Team;
import com.projecthub.repository.custom.CustomTeamRepository;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;
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

import com.projecthub.config.CSVProperties;

/**
 * CSV implementation of the {@link CustomTeamRepository} interface.
 */
@Repository("csvTeamRepository")
public abstract class CSVTeamRepository implements CustomTeamRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVTeamRepository.class);

    private final CSVProperties csvProperties;
    private final Validator validator;


    public CSVTeamRepository(CSVProperties csvProperties, Validator validator) {
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
        java.nio.file.Path source = java.nio.file.Path.of(filePath);
        java.nio.file.Path backup = java.nio.file.Path.of(filePath + ".backup");
        java.nio.file.Files.copy(source, backup, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created for file: {}", filePath);
    }

    /**
     * Validates a {@link Team} object.
     *
     * @param team the {@code Team} object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateTeam(Team team) {
        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Team> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Team validation failed: " + sb.toString());
        }
    }

    /**
     * Saves a team to the CSV file after validation and backup.
     *
     * @param team the {@code Team} object to save
     * @return the saved {@code Team} object
     * @throws RuntimeException if an error occurs during saving
     */
    @Override
    public Team save(Team team) {
        validateTeam(team);
        try {
            backupCSVFile(csvProperties.getTeamsFilepath());
            List<Team> teams = findAll();
            teams.removeIf(t -> t.getId().equals(team.getId()));
            teams.add(team);
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getTeamsFilepath()))) {
                ColumnPositionMappingStrategy<Team> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Team.class);
                String[] memberFieldsToBindTo = {"id", "name"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Team> beanToCsv = new StatefulBeanToCsvBuilder<Team>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(teams);
            }
            logger.info("Team saved successfully: {}", team);
            return team;
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException | com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error saving team to CSV", e);
            throw new RuntimeException("Error saving team to CSV", e);
        }
    }

    /**
     * Retrieves all teams from the CSV file.
     *
     * @return a list of {@code Team} objects
     */
    @Override
    public List<Team> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getTeamsFilepath()))) {
            ColumnPositionMappingStrategy<Team> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Team.class);
            String[] memberFieldsToBindTo = {"id", "name"};
            strategy.setColumnMapping(memberFieldsToBindTo);
            return new CsvToBeanBuilder<Team>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            logger.error("Error reading teams from CSV", e);
            throw new RuntimeException("Error reading teams from CSV", e);
        }
    }

    /**
     * Finds a team by their ID.
     *
     * @param id the ID of the team
     * @return an {@code Optional} containing the team if found, or empty if not found
     */
    @Override
    public Optional<Team> findById(Long id) {
        return findAll().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    /**
     * Deletes a team by their ID.
     *
     * @param id the ID of the team to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(Long id) {
        try {
            backupCSVFile(csvProperties.getTeamsFilepath());
            List<Team> teams = findAll();
            teams.removeIf(t -> t.getId().equals(id));
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getTeamsFilepath()))) {
                ColumnPositionMappingStrategy<Team> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Team.class);
                String[] memberFieldsToBindTo = {"id", "name"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Team> beanToCsv = new StatefulBeanToCsvBuilder<Team>(writer)
                        .withMappingStrategy(strategy)
                        .build();
                beanToCsv.write(teams);
            }
            logger.info("Team deleted successfully: {}", id);
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException | com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting team from CSV", e);
            throw new RuntimeException("Error deleting team from CSV", e);
        }
    }
}