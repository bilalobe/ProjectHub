package projecthub.csv.plugin.impl;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.projecthub.base.models.Team;
import com.projecthub.base.repositories.csv.TeamCsvRepository;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import projecthub.csv.plugin.config.CsvProperties;
import projecthub.csv.plugin.repository.AbstractCsvRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Repository("csvTeamRepository")
@Profile("csv")
public class TeamCsvRepositoryImpl extends AbstractCsvRepository<Team> implements TeamCsvRepository {

    public TeamCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
        super(csvProperties.getTeamsFilepath(),
              Team.class,
              createMappingStrategy(),
              validator);
    }

    private static ColumnPositionMappingStrategy<Team> createMappingStrategy() {
        ColumnPositionMappingStrategy<Team> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Team.class);
        strategy.setColumnMapping("id", "name", "cohortId", "description");
        return strategy;
    }

    @Override
    protected UUID getEntityId(Team team) {
        return team.getId();
    }

    @Override
    protected void validateEntity(Team team) {
        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Team validation failed: " +
                violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", ")));
        }
    }

    @Override
    public List<Team> findByCohortId(UUID cohortId) {
        return findAll().stream()
            .filter(t -> t.getCohort() != null &&
                        Objects.equals(t.getCohort().getId(), cohortId))
            .toList();
    }
}
