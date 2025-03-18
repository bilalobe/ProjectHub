package projecthub.csv.plugin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import projecthub.csv.plugin.config.CsvProperties;
import projecthub.csv.plugin.service.CsvInitializationService;

import java.io.File;
import java.nio.file.Path;

@SpringBootTest
@ActiveProfiles("test")
class CsvPluginApplicationTests {

    @Autowired
    private CsvProperties csvProperties;

    @Autowired
    private CsvInitializationService initializationService;

    @TempDir
    Path tempDir;

    CsvPluginApplicationTests() {
    }

    @BeforeEach
    void setup() {
        csvProperties.setBasePath(tempDir.resolve("csv").toString());
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(csvProperties);
        Assertions.assertNotNull(initializationService);
    }

    @Test
    void shouldInitializeAllCsvFiles() throws Exception {
        initializationService.afterPropertiesSet();

        Assertions.assertTrue(new File(csvProperties.getComponentsFilepath()).exists());
        Assertions.assertTrue(new File(csvProperties.getProjectsFilepath()).exists());
        Assertions.assertTrue(new File(csvProperties.getSchoolsFilepath()).exists());
        Assertions.assertTrue(new File(csvProperties.getStudentsFilepath()).exists());
        Assertions.assertTrue(new File(csvProperties.getSubmissionsFilepath()).exists());
        Assertions.assertTrue(new File(csvProperties.getTasksFilepath()).exists());
        Assertions.assertTrue(new File(csvProperties.getTeamsFilepath()).exists());
        Assertions.assertTrue(new File(csvProperties.getCohortsFilepath()).exists());
        Assertions.assertTrue(new File(csvProperties.getUsersFilepath()).exists());
    }
}
