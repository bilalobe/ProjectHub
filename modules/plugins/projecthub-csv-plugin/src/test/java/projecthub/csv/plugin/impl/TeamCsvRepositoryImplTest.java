package projecthub.csv.plugin.impl;

import com.projecthub.base.models.Cohort;
import com.projecthub.base.models.Team;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import projecthub.csv.plugin.config.CsvProperties;
import projecthub.csv.plugin.config.TestCsvConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("csv")
@Import(TestCsvConfig.class)
class TeamCsvRepositoryImplTest {

    @Autowired
    private TeamCsvRepositoryImpl teamRepository;

    @Autowired
    private CsvProperties csvProperties;

    private Team testTeam;
    private Cohort testCohort;

    TeamCsvRepositoryImplTest() {
    }

    @BeforeEach
    void setUp() throws java.io.IOException {
        // Ensure clean state for each test
        Files.createDirectories(Path.of(csvProperties.getTeamsFilepath()).getParent());
        Files.deleteIfExists(Path.of(csvProperties.getTeamsFilepath()));

        testCohort = new Cohort();
        testCohort.setId(UUID.randomUUID());
        testCohort.setName("Test Cohort");

        testTeam = new Team();
        testTeam.setId(UUID.randomUUID());
        testTeam.setName("Test Team");
        testTeam.setCohort(testCohort);
        testTeam.setDescription("Test Description");
    }

    @Test
    void whenSaveTeam_thenTeamIsPersisted() {
        Team savedTeam = teamRepository.save(testTeam);
        Assertions.assertNotNull(savedTeam);
        assertEquals(testTeam.getId(), savedTeam.getId());
        assertEquals(testTeam.getName(), savedTeam.getName());
    }

    @Test
    void whenFindById_thenReturnsCorrectTeam() {
        teamRepository.save(testTeam);
        Optional<Team> found = teamRepository.findById(testTeam.getId());
        Assertions.assertTrue(found.isPresent());
        assertEquals(testTeam.getName(), found.get().getName());
    }

    @Test
    void whenFindAll_thenReturnsAllTeams() {
        Team team2 = new Team();
        team2.setId(UUID.randomUUID());
        team2.setName("Test Team 2");
        team2.setCohort(testCohort);

        teamRepository.save(testTeam);
        teamRepository.save(team2);

        List<Team> teams = teamRepository.findAll();
        Assertions.assertEquals(2, teams.size());
        Assertions.assertTrue(teams.stream().anyMatch(t -> t.getId().equals(testTeam.getId())));
        Assertions.assertTrue(teams.stream().anyMatch(t -> t.getId().equals(team2.getId())));
    }

    @Test
    void whenDeleteById_thenTeamIsRemoved() {
        teamRepository.save(testTeam);
        org.assertj.core.api.Assertions.assertThat(teamRepository.findById(testTeam.getId())).isPresent();

        teamRepository.deleteById(testTeam.getId());
        org.assertj.core.api.Assertions.assertThat(teamRepository.findById(testTeam.getId())).isEmpty();
    }

    @Test
    void whenFindByCohortId_thenReturnsCorrectTeams() {
        Team team2 = new Team();
        team2.setId(UUID.randomUUID());
        team2.setName("Test Team 2");
        team2.setCohort(testCohort);

        Cohort otherCohort = new Cohort();
        otherCohort.setId(UUID.randomUUID());
        Team teamOtherCohort = new Team();
        teamOtherCohort.setId(UUID.randomUUID());
        teamOtherCohort.setName("Other Cohort Team");
        teamOtherCohort.setCohort(otherCohort);

        teamRepository.save(testTeam);
        teamRepository.save(team2);
        teamRepository.save(teamOtherCohort);

        List<Team> cohortTeams = teamRepository.findByCohortId(testCohort.getId());
        Assertions.assertEquals(2, cohortTeams.size());
        Assertions.assertTrue(cohortTeams.stream().allMatch(t -> t.getCohort().getId().equals(testCohort.getId())));
    }

    @Test
    void whenSaveTeamWithNullRequiredField_thenThrowsException() {
        testTeam.setName(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> teamRepository.save(testTeam));
    }
}
