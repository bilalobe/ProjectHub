package projecthub.csv.plugin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import projecthub.csv.plugin.config.CsvProperties;
import projecthub.csv.plugin.helper.CsvFileHelper;
import projecthub.csv.plugin.helper.CsvValidator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CsvMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(CsvMigrationService.class);
    private static final String VERSION_FILE = "csv_schema_version.txt";
    private static final Map<Integer, Map<String, String[]>> SCHEMA_VERSIONS = new HashMap<>();

    private final CsvProperties csvProperties;
    private final CsvFileHelper fileHelper;
    private final CsvValidator validator;
    private final Map<String, Integer> currentVersions = new ConcurrentHashMap<>();

    static {
        // Define schema versions and their column mappings
        Map<String, String[]> v1Schema = new HashMap<>();
        v1Schema.put("teams", new String[]{"id", "name"});
        v1Schema.put("projects", new String[]{"id", "name", "description"});
        SCHEMA_VERSIONS.put(Integer.valueOf(1), v1Schema);

        Map<String, String[]> v2Schema = new HashMap<>();
        v2Schema.put("teams", new String[]{"id", "name", "cohortId", "description"});
        v2Schema.put("projects", new String[]{"id", "name", "description", "status"});
        SCHEMA_VERSIONS.put(Integer.valueOf(2), v2Schema);
    }

    public CsvMigrationService(CsvProperties csvProperties,
                             CsvFileHelper fileHelper,
                             CsvValidator validator) {
        this.csvProperties = csvProperties;
        this.fileHelper = fileHelper;
        this.validator = validator;
    }

    public void migrateIfNeeded() {
        try {
            Path versionFile = getVersionFilePath();
            int currentVersion = getCurrentVersion(versionFile);
            int latestVersion = SCHEMA_VERSIONS.keySet().stream()
                                            .mapToInt(Integer::intValue)
                                            .max()
                                            .orElse(0);

            if (currentVersion < latestVersion) {
                logger.info("Starting CSV schema migration from version {} to {}",
                        Integer.valueOf(currentVersion), Integer.valueOf(latestVersion));

                for (int version = currentVersion + 1; version <= latestVersion; version++) {
                    migrateToVersion(version);
                }

                updateVersionFile(versionFile, latestVersion);
                logger.info("CSV schema migration completed successfully");
            }
        } catch (Exception e) {
            logger.error("Failed to perform CSV schema migration", e);
            throw new RuntimeException("CSV schema migration failed", e);
        }
    }

    private void migrateToVersion(int targetVersion) {
        Map<String, String[]> schema = SCHEMA_VERSIONS.get(Integer.valueOf(targetVersion));
        if (schema == null) {
            throw new IllegalStateException("No schema defined for version " + targetVersion);
        }

        schema.forEach((entity, columns) -> {
            String filepath = getFilepathForEntity(entity);
            if (filepath != null) {
                try {
                    fileHelper.backupCSVFile(filepath);
                    validator.validateCsvStructure(filepath, columns);
                    logger.info("Migrated {} to version {}", entity, Integer.valueOf(targetVersion));
                } catch (RuntimeException e) {
                    logger.error("Failed to migrate {} to version {}", entity, Integer.valueOf(targetVersion), e);
                    throw new RuntimeException(
                        String.format("Failed to migrate %s to version %d", entity, Integer.valueOf(targetVersion)), e);
                }
            }
        });
    }

    private String getFilepathForEntity(String entity) {
        return switch (entity) {
            case "teams" -> csvProperties.getTeamsFilepath();
            case "projects" -> csvProperties.getProjectsFilepath();
            case "schools" -> csvProperties.getSchoolsFilepath();
            case "students" -> csvProperties.getStudentsFilepath();
            case "submissions" -> csvProperties.getSubmissionsFilepath();
            case "tasks" -> csvProperties.getTasksFilepath();
            case "cohorts" -> csvProperties.getCohortsFilepath();
            case "users" -> csvProperties.getUsersFilepath();
            default -> null;
        };
    }

    private Path getVersionFilePath() {
        String dataDir = new File(csvProperties.getTeamsFilepath()).getParent();
        return Path.of(dataDir, VERSION_FILE);
    }

    private static int getCurrentVersion(Path versionFile) throws java.io.IOException, NumberFormatException {
        if (!Files.exists(versionFile)) {
            return 0;
        }
        String version = Files.readString(versionFile).trim();
        return Integer.parseInt(version);
    }

    private static void updateVersionFile(Path versionFile, int version) throws java.io.IOException {
        Files.writeString(versionFile, String.valueOf(version));
    }
}
