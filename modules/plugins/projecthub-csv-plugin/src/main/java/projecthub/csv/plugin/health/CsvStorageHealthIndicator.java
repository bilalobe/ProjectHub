package projecthub.csv.plugin.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import projecthub.csv.plugin.config.CsvProperties;
import projecthub.csv.plugin.helper.CsvFileHelper;
import projecthub.csv.plugin.helper.CsvValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Component
public class CsvStorageHealthIndicator implements HealthIndicator {
    private final CsvProperties csvProperties;
    private final CsvFileHelper fileHelper;
    private final CsvValidator validator;

    public CsvStorageHealthIndicator(CsvProperties csvProperties,
                                   CsvFileHelper fileHelper,
                                   CsvValidator validator) {
        this.csvProperties = csvProperties;
        this.fileHelper = fileHelper;
        this.validator = validator;
    }

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        boolean isHealthy = true;

        // Check each CSV file
        isHealthy &= checkFile("teams", csvProperties.getTeamsFilepath(), details);
        isHealthy &= checkFile("projects", csvProperties.getProjectsFilepath(), details);
        isHealthy &= checkFile("schools", csvProperties.getSchoolsFilepath(), details);
        isHealthy &= checkFile("students", csvProperties.getStudentsFilepath(), details);
        isHealthy &= checkFile("submissions", csvProperties.getSubmissionsFilepath(), details);
        isHealthy &= checkFile("tasks", csvProperties.getTasksFilepath(), details);
        isHealthy &= checkFile("cohorts", csvProperties.getCohortsFilepath(), details);
        isHealthy &= checkFile("users", csvProperties.getUsersFilepath(), details);

        return isHealthy
            ? Health.up().withDetails(details).build()
            : Health.down().withDetails(details).build();
    }

    private boolean checkFile(String entity, String filepath, Map<String, Object> details) {
        Map<String, Object> fileDetails = new HashMap<>();
        boolean isHealthy = true;

        try {
            File file = new File(filepath);
            fileDetails.put("exists", Boolean.valueOf(file.exists()));
            fileDetails.put("writable", Boolean.valueOf(Files.isWritable(file.toPath())));
            fileDetails.put("readable", Boolean.valueOf(Files.isReadable(file.toPath())));
            fileDetails.put("size", Long.valueOf(Files.size(file.toPath())));
            fileDetails.put("locked", Boolean.valueOf(fileHelper.isFileLocked(filepath)));

            // Basic validation of file structure
            if (file.exists() && Files.size(file.toPath()) > 0L) {
                try {
                    validator.validateCsvStructure(filepath, getExpectedColumns(entity));
                    fileDetails.put("valid", Boolean.TRUE);
                } catch (RuntimeException e) {
                    fileDetails.put("valid", Boolean.FALSE);
                    fileDetails.put("validationError", e.getMessage());
                    isHealthy = false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            fileDetails.put("error", e.getMessage());
            isHealthy = false;
        }

        details.put(entity, fileDetails);
        return isHealthy;
    }

    private static String[] getExpectedColumns(String entity) {
        return switch (entity) {
            case "teams" -> new String[]{"id", "name", "cohortId", "description"};
            case "projects" -> new String[]{"id", "name", "description", "status"};
            case "schools" -> new String[]{"id", "name", "location"};
            case "students" -> new String[]{"id", "name", "email", "schoolId"};
            case "submissions" -> new String[]{"id", "studentId", "taskId", "submissionDate", "status"};
            case "tasks" -> new String[]{"id", "name", "description", "dueDate"};
            case "cohorts" -> new String[]{"id", "name", "startDate", "endDate"};
            case "users" -> new String[]{"id", "username", "email", "role"};
            default -> new String[]{};
        };
    }
}
