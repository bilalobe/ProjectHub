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
import com.projecthub.model.Team;
import com.projecthub.repository.custom.CustomTeamRepository;

@Repository("csvTeamRepository")
public abstract class CSVTeamRepository implements CustomTeamRepository {

    @Value("${app.teams.filepath}")
    private String teamsFilePath;

    @Override
    public Team save(Team team) {
        try {
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

            return team;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
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

    @Override
    public void deleteById(Long teamId) {
        try {
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
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException("Error deleting team from CSV", e);
        }
    }
}