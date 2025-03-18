package projecthub.csv.plugin.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import projecthub.csv.plugin.config.CsvProperties;
import projecthub.csv.plugin.config.TestCsvConfig;
import projecthub.csv.plugin.helper.CsvFileHelper;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

@SpringBootTest
@ActiveProfiles("csv")
@Import(TestCsvConfig.class)
class CsvMaintenanceAndMigrationTest {

    private static final Pattern PATTERN = Pattern.compile("id,name,description.*");
    @Autowired
    private CsvMaintenanceService maintenanceService;

    @Autowired
    private CsvMigrationService migrationService;

    @Autowired
    private CsvProperties csvProperties;

    @Autowired
    private CsvFileHelper fileHelper;

    CsvMaintenanceAndMigrationTest() {
    }

    @BeforeEach
    void setUp() throws java.io.IOException {
        // Create test directories and files
        Files.createDirectories(Path.of(csvProperties.getTeamsFilepath()).getParent());
    }

    @Test
    void whenMaintenanceRuns_thenFilesAreNormalized() throws java.io.IOException {
        // Create a file with mixed line endings and blank lines
        String content = "id,name,description\r\n\n" +
                        "1,Team 1,Desc 1\r\n\n" +
                        "2,Team 2,Desc 2\n\n";

        try (FileWriter writer = new FileWriter(csvProperties.getTeamsFilepath())) {
            writer.write(content);
        }

        maintenanceService.performMaintenance();

        List<String> lines = Files.readAllLines(Path.of(csvProperties.getTeamsFilepath()));
        Assertions.assertEquals(3, lines.size()); // Header + 2 data lines, no blank lines
        Assertions.assertTrue(PATTERN.matcher(lines.get(0)).matches()); // Header check
    }

    @Test
    void whenMigrationRuns_thenSchemaIsUpdated() throws java.io.IOException {
        // Create old schema version file
        String oldSchema = "id,name\n" +
                         "1,Team 1\n" +
                         "2,Team 2\n";

        try (FileWriter writer = new FileWriter(csvProperties.getTeamsFilepath())) {
            writer.write(oldSchema);
        }

        // Run migration
        migrationService.migrateIfNeeded();

        // Verify new schema
        List<String> lines = Files.readAllLines(Path.of(csvProperties.getTeamsFilepath()));
        Assertions.assertTrue(lines.get(0).contains("cohortId")); // New column should be present
        Assertions.assertTrue(lines.get(0).contains("description")); // New column should be present
    }

    @Test
    void whenMigrationAndMaintenanceRun_thenFilesAreValidAndNormalized() throws java.io.IOException {
        // Create old schema with inconsistent formatting
        String oldContent = "id,name\r\n\n" +
                          "1,Team 1\r\n\n" +
                          "2,Team 2\n\n";

        try (FileWriter writer = new FileWriter(csvProperties.getTeamsFilepath())) {
            writer.write(oldContent);
        }

        // Run both services
        migrationService.migrateIfNeeded();
        maintenanceService.performMaintenance();

        // Verify results
        List<String> lines = Files.readAllLines(Path.of(csvProperties.getTeamsFilepath()));
        Assertions.assertTrue(lines.stream().noneMatch(String::isEmpty)); // No blank lines
        Assertions.assertTrue(lines.get(0).contains("cohortId")); // Updated schema
        Assertions.assertTrue(lines.get(0).contains("description")); // Updated schema
        Assertions.assertEquals(3, lines.size()); // Header + 2 data lines
    }

    @Test
    void whenBackupCreated_thenOriginalFileIsPreserved() throws java.io.IOException {
        String originalContent = "id,name\n1,Team 1\n2,Team 2\n";

        try (FileWriter writer = new FileWriter(csvProperties.getTeamsFilepath())) {
            writer.write(originalContent);
        }

        // Get backup file count before operations
        Path backupDir = Path.of(csvProperties.getTeamsFilepath()).getParent().resolve("backups");
        int initialBackups = Files.exists(backupDir) ?
            Files.list(backupDir).toList().size() : 0;

        // Run operations that should create backups
        migrationService.migrateIfNeeded();
        maintenanceService.performMaintenance();

        // Verify backups were created
        Assertions.assertTrue(Files.exists(backupDir));
        int finalBackups = Files.list(backupDir).toList().size();
        Assertions.assertTrue(finalBackups > initialBackups);
    }
}
