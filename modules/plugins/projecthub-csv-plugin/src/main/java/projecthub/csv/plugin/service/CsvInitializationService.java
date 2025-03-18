package projecthub.csv.plugin.service;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import projecthub.csv.plugin.config.CsvProperties;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class CsvInitializationService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(CsvInitializationService.class);
    private final CsvProperties csvProperties;

    public CsvInitializationService(CsvProperties csvProperties) {
        this.csvProperties = csvProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initializeAllCsvFiles();
    }

    private void initializeAllCsvFiles() {
        initializeCsvFile(csvProperties.getComponentsFilepath(), new String[]{"id", "name", "description", "projectId"});
        initializeCsvFile(csvProperties.getProjectsFilepath(), new String[]{"id", "name", "description", "teamId", "deadline", "startDate", "endDate", "status"});
        initializeCsvFile(csvProperties.getSchoolsFilepath(), new String[]{"id", "name"});
        initializeCsvFile(csvProperties.getStudentsFilepath(), new String[]{"id", "name", "teamId"});
        initializeCsvFile(csvProperties.getSubmissionsFilepath(), new String[]{"id", "studentId", "projectId", "submissionDate", "content"});
        initializeCsvFile(csvProperties.getTasksFilepath(), new String[]{"id", "name", "description", "projectId"});
        initializeCsvFile(csvProperties.getTeamsFilepath(), new String[]{"id", "name"});
        initializeCsvFile(csvProperties.getCohortsFilepath(), new String[]{"id", "name", "schoolId"});
        initializeCsvFile(csvProperties.getUsersFilepath(), new String[]{"id", "username", "email", "firstName", "lastName", "teamId"});
    }

    private static void initializeCsvFile(String filePath, String[] headers) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        if (!file.exists()) {
            try {
                if (parentDir != null && !parentDir.exists()) {
                    if (!parentDir.mkdirs()) {
                        logger.error("Failed to create directory: {}", parentDir.getAbsolutePath());
                        return;
                    }
                }

                try (ICSVWriter writer = new CSVWriter(new FileWriter(file))) {
                    writer.writeNext(headers);
                    logger.info("Initialized CSV file with headers: {}", filePath);
                }
            } catch (IOException e) {
                logger.error("Error initializing CSV file: {}", filePath, e);
            }
        }
    }
}
