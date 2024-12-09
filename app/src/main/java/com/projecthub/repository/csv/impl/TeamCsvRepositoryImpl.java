package com.projecthub.repository.csv.impl;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.projecthub.config.CsvProperties;
import com.projecthub.model.Team;
import com.projecthub.repository.csv.TeamCsvRepository;
import com.projecthub.repository.csv.helper.CsvHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository("csvTeamRepository")
@Profile("csv")
public class TeamCsvRepositoryImpl implements TeamCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(TeamCsvRepositoryImpl.class);

    private final CsvProperties csvProperties;
    private final Validator validator;

    public TeamCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
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
            throw new IllegalArgumentException("Team validation failed: " + sb);
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
            String[] columns = {"id", "name"};
            CsvHelper.writeBeansToCsv(csvProperties.getTeamsFilepath(), Team.class, teams, columns);
            logger.info("Team saved successfully: {}", team);
            return team;
        } catch (Exception e) {
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
    public Optional<Team> findById(UUID id) {
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
    public void deleteById(UUID id) {
        try {
            backupCSVFile(csvProperties.getTeamsFilepath());
            List<Team> teams = findAll();
            teams.removeIf(t -> t.getId().equals(id));
            String[] columns = {"id", "name"};
            CsvHelper.writeBeansToCsv(csvProperties.getTeamsFilepath(), Team.class, teams, columns);
            logger.info("Team deleted successfully: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting team from CSV", e);
            throw new RuntimeException("Error deleting team from CSV", e);
        }
    }

    /**
     * Finds teams by their cohort ID.
     *
     * @param cohortId the ID of the cohort
     * @return a list of {@code Team} objects belonging to the cohort
     */
    @Override
    public List<Team> findByCohortId(UUID cohortId) {
        return findAll().stream()
                .filter(t -> t.getCohort().getId().equals(cohortId))
                .toList();
    }
}