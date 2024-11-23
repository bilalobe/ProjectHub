package com.projecthub.repository.csv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
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
import com.projecthub.model.Team;
import com.projecthub.repository.custom.CustomTeamRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * CSV implementation of the CustomTeamRepository interface.
 */
@Repository("csvTeamRepository")
public abstract class CSVTeamRepository implements CustomTeamRepository {

    private static final Logger logger = LoggerFactory.getLogger(CSVTeamRepository.class);
    private final Validator validator;

    @Value("${app.teams.filepath}")
    private String teamsFilePath;

    public CSVTeamRepository() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private void backupCSVFile(String filePath) throws IOException {
        Path source = Path.of(filePath);
        Path backup = Path.of(filePath + ".backup");
        Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created for file: {}", filePath);
    }

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
     * @param team the Team object to save
     * @return the saved Team object
     */
    @Override
    public Team save(Team team) {
        validateTeam(team);
        try {
            backupCSVFile(teamsFilePath);
            List<Team> teams = findAll();
            teams.removeIf(t -> t.getId().equals(team.getId()));
            teams.add(team);

            try (CSVWriter writer = new CSVWriter(new FileWriter(teamsFilePath))) {
                ColumnPositionMappingStrategy<Team> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Team.class);
                String[] memberFieldsToBindTo = {"id", "name", "schoolId"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<Team> beanToCsv = new StatefulBeanToCsvBuilder<Team>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(teams);
            }
            logger.info("Team saved successfully: {}", team);
            return team;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error saving team to CSV", e);
            throw new RuntimeException("Error saving team to CSV", e);
        }
    }

    @Override
    public List<Team> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(teamsFilePath))) {
            ColumnPositionMappingStrategy<Team> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Team.class);
            String[] memberFieldsToBindTo = {"id", "name", "schoolId"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            return new CsvToBeanBuilder<Team>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException("Error reading teams from CSV", e);
        }
    }

    /**
     * Deletes a team by its ID.
     *
     * @param teamId the ID of the team to delete
     */
    @Override
    public void deleteById(Long teamId) {
        try {
            backupCSVFile(teamsFilePath);
            List<Team> teams = findAll();
            teams.removeIf(team -> team.getId().equals(teamId));

            try (CSVWriter writer = new CSVWriter(new FileWriter(teamsFilePath))) {
                ColumnPositionMappingStrategy<Team> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Team.class);
                String[] memberFieldsToBindTo = {"id", "name", "schoolId"};
                strategy.setColumnMapping(memberFieldsToBindTo);

                StatefulBeanToCsv<Team> beanToCsv = new StatefulBeanToCsvBuilder<Team>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(teams);
            }
            logger.info("Team deleted successfully: {}", teamId);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting team from CSV", e);
            throw new RuntimeException("Error deleting team from CSV", e);
        }
    }
}