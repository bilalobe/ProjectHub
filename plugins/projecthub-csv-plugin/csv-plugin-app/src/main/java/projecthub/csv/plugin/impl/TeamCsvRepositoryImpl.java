package projecthub.csv.plugin.repository.impl;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import projecthub.csv.plugin.config.CsvProperties;
import com.projecthub.core.models.Team;
import com.projecthub.core.repositories.csv.TeamCsvRepository;
import projecthub.csv.plugin.helper.CsvFileHelper;
import projecthub.csv.plugin.helper.CsvHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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
            CsvFileHelper.backupCSVFile(csvProperties.getTeamsFilepath());
            List<Team> teams = findAll();
            teams.removeIf(t -> Objects.equals(t.getId(), team.getId()));
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
                .filter(t -> Objects.equals(t.getId(), id))
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
            CsvFileHelper.backupCSVFile(csvProperties.getTeamsFilepath());
            List<Team> teams = findAll();
            teams.removeIf(t -> Objects.equals(t.getId(), id));
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
                .filter(t -> t.getCohort() != null && Objects.equals(t.getCohort().getId(), cohortId))
                .toList();
    }
}