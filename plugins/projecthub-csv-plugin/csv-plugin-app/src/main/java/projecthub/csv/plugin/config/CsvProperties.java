package projecthub.csv.plugin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for CSV file locations.
 * Manages the file paths for all CSV data sources used in the application.
 * All properties are validated to ensure they are not blank.
 *
 * @since 1.0.0
 */
@Setter
@Getter
@Configuration
@Validated
@ConfigurationProperties(prefix = "csv.storage")
public class CsvProperties {

    /**
     * Path to the components CSV file.
     * Defaults to "data/components.csv" if not specified.
     */
    @NotBlank(message = "Components filepath must not be blank")
    private String componentsFilepath = "data/components.csv";

    @NotBlank(message = "Projects filepath must not be blank")
    private String projectsFilepath = "data/projects.csv";

    @NotBlank(message = "Schools filepath must not be blank")
    private String schoolsFilepath;

    @NotBlank(message = "Students filepath must not be blank")
    private String studentsFilepath;

    @NotBlank(message = "Submissions filepath must not be blank")
    private String submissionsFilepath;

    @NotBlank(message = "Tasks filepath must not be blank")
    private String tasksFilepath;

    @NotBlank(message = "Teachers filepath must not be blank")
    private String teachersFilepath;

    @NotBlank(message = "Teams filepath must not be blank")
    private String teamsFilepath;

    @NotBlank(message = "Cohorts filepath must not be blank")
    private String cohortsFilepath;

    @NotBlank(message = "Users filepath must not be blank")
    private String usersFilepath;

    public String getCohortsFilepath() {
        return null;
    }
}