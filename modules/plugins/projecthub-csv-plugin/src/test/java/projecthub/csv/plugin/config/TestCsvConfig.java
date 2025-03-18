package projecthub.csv.plugin.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import projecthub.csv.plugin.helper.CsvFileHelper;
import projecthub.csv.plugin.helper.CsvValidator;

import java.nio.file.Files;
import java.nio.file.Path;

@TestConfiguration
public class TestCsvConfig {

    public TestCsvConfig() {
    }

    @Bean
    @Primary
    public CsvProperties testCsvProperties() throws java.io.IOException {
        // Create temporary test directory
        Path testDir = Files.createTempDirectory("csv-test");

        CsvProperties properties = new CsvProperties();
        properties.setTeamsFilepath(testDir.resolve("teams.csv").toString());
        properties.setProjectsFilepath(testDir.resolve("projects.csv").toString());
        properties.setSchoolsFilepath(testDir.resolve("schools.csv").toString());
        properties.setStudentsFilepath(testDir.resolve("students.csv").toString());
        properties.setSubmissionsFilepath(testDir.resolve("submissions.csv").toString());
        properties.setTasksFilepath(testDir.resolve("tasks.csv").toString());
        properties.setTeachersFilepath(testDir.resolve("teachers.csv").toString());
        properties.setCohortsFilepath(testDir.resolve("cohorts.csv").toString());
        properties.setUsersFilepath(testDir.resolve("users.csv").toString());

        return properties;
    }

    @Bean
    @Primary
    public CsvFileHelper testCsvFileHelper() {
        return new CsvFileHelper(); // Use real implementation for integration tests
    }

    @Bean
    @Primary
    public CsvValidator testCsvValidator() {
        return new CsvValidator(); // Use real implementation for integration tests
    }
}
