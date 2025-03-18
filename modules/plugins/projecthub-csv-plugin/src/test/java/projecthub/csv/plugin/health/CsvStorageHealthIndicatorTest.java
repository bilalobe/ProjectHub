package projecthub.csv.plugin.health;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import projecthub.csv.plugin.config.CsvProperties;
import projecthub.csv.plugin.config.TestCsvConfig;
import projecthub.csv.plugin.helper.CsvHelper;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
@ActiveProfiles("csv")
@Import(TestCsvConfig.class)
class CsvStorageHealthIndicatorTest {

    @Autowired
    private CsvStorageHealthIndicator healthIndicator;

    @Autowired
    private CsvProperties csvProperties;

    @Autowired
    private CsvHelper csvHelper;

    CsvStorageHealthIndicatorTest() {
    }

    @BeforeEach
    void setUp() throws Exception {
        // Create test directories and files
        createTestFiles();
    }

    @Test
    void whenAllFilesValid_thenHealthIsUp() throws Exception {
        // Create valid CSV files
        writeValidCsvContent(csvProperties.getTeamsFilepath());
        writeValidCsvContent(csvProperties.getProjectsFilepath());

        Health health = healthIndicator.health();
        Assertions.assertEquals(Status.UP, health.getStatus());
    }

    @Test
    void whenFilesMissing_thenHealthIsDown() throws Exception {
        // Delete all CSV files
        deleteAllCsvFiles();

        Health health = healthIndicator.health();
        Assertions.assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    void whenFilesInvalid_thenHealthIsDown() throws java.io.IOException {
        // Write invalid content to CSV file
        try (FileWriter writer = new FileWriter(csvProperties.getTeamsFilepath())) {
            writer.write("invalid,csv,content\n");
        }

        Health health = healthIndicator.health();
        Assertions.assertEquals(Status.DOWN, health.getStatus());
    }

    private void createTestFiles() throws java.io.IOException {
        Files.createDirectories(Path.of(csvProperties.getTeamsFilepath()).getParent());
        Files.createDirectories(Path.of(csvProperties.getProjectsFilepath()).getParent());
    }

    private static void writeValidCsvContent(String filepath) throws java.io.IOException {
        String header = "id,name,description\n";
        String content = "123e4567-e89b-12d3-a456-426614174000,Test Name,Test Description\n";

        try (FileWriter writer = new FileWriter(filepath)) {
            writer.write(header);
            writer.write(content);
        }
    }

    private void deleteAllCsvFiles() {
        new File(csvProperties.getTeamsFilepath()).delete();
        new File(csvProperties.getProjectsFilepath()).delete();
        // Add other files as needed
    }
}
