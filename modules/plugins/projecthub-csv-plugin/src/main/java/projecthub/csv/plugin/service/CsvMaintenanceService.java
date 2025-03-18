package projecthub.csv.plugin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import projecthub.csv.plugin.config.CsvProperties;
import projecthub.csv.plugin.helper.CsvFileHelper;
import projecthub.csv.plugin.helper.CsvValidator;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class CsvMaintenanceService {
    private static final Logger logger = LoggerFactory.getLogger(CsvMaintenanceService.class);

    private final CsvProperties csvProperties;
    private final CsvFileHelper fileHelper;
    private final CsvValidator validator;

    public CsvMaintenanceService(CsvProperties csvProperties,
                               CsvFileHelper fileHelper,
                               CsvValidator validator) {
        this.csvProperties = csvProperties;
        this.fileHelper = fileHelper;
        this.validator = validator;
    }

    @Scheduled(fixedDelay = 24L, timeUnit = TimeUnit.HOURS)
    public void performMaintenance() {
        logger.info("Starting scheduled CSV maintenance");

        List<String> files = Arrays.asList(
            csvProperties.getTeamsFilepath(),
            csvProperties.getProjectsFilepath(),
            csvProperties.getSchoolsFilepath(),
            csvProperties.getStudentsFilepath(),
            csvProperties.getSubmissionsFilepath(),
            csvProperties.getTasksFilepath(),
            csvProperties.getCohortsFilepath(),
            csvProperties.getUsersFilepath()
        );

        for (String filepath : files) {
            try {
                maintainFile(filepath);
            } catch (Exception e) {
                logger.error("Error during maintenance of file: {}", filepath, e);
            }
        }

        logger.info("CSV maintenance completed");
    }

    private void maintainFile(String filepath) throws Exception {
        File file = new File(filepath);
        if (!file.exists()) {
            logger.warn("File does not exist: {}", filepath);
            return;
        }

        // Create backup before maintenance
        fileHelper.backupCSVFile(filepath);

        // Check and repair file permissions
        ensureFilePermissions(file);

        // Validate file structure
        validator.validateCsvStructure(filepath, getExpectedColumns(filepath));

        // Remove empty lines and normalize line endings
        normalizeFile(file);

        logger.info("Maintenance completed for file: {}", filepath);
    }

    private static void ensureFilePermissions(File file) throws Exception {
        if (!file.canRead() || !file.canWrite()) {
            file.setReadable(true);
            file.setWritable(true);
            logger.info("Fixed file permissions for: {}", file.getPath());
        }
    }

    private static void normalizeFile(File file) throws java.io.IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        lines.removeIf(String::isBlank);
        Files.write(file.toPath(), lines);
    }

    private static String[] getExpectedColumns(String filepath) {
        if (filepath.endsWith("teams.csv")) {
            return new String[]{"id", "name", "cohortId", "description"};
        } else if (filepath.endsWith("projects.csv")) {
            return new String[]{"id", "name", "description", "status"};
        }
        // Add other file schemas as needed
        return new String[]{};
    }
}
