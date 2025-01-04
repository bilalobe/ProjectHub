package projecthub.csv.plugin.impl;

@Repository("csvProjectRepository")
@Profile("csv")
public class ProjectCsvRepositoryImpl implements ProjectCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProjectCsvRepositoryImpl.class);

    private final Validator validator;
    private final CsvProperties csvProperties;

    public ProjectCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
        this.csvProperties = csvProperties;
        this.validator = validator;
    }

    /**
     * Validates a {@link Project} object.
     *
     * @param project the {@code Project} object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateProject(Project project) {
        Set<ConstraintViolation<Project>> violations = validator.validate(project);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Project> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Project validation failed: " + sb);
        }
    }

    /**
     * Saves a project to the CSV file after validation and backup.
     *
     * @param project the {@code Project} object to save
     * @return the saved {@code Project} object
     * @throws RuntimeException if an error occurs during saving
     */
    @Override
    public Project save(Project project) {
        validateProject(project);
        try {
            CsvFileHelper.backupCSVFile(csvProperties.getProjectsFilepath());
            List<Project> projects = findAll();
            projects.removeIf(p -> Objects.equals(p.getId(), project.getId()));
            projects.add(project);
            String[] columns = {"id", "name", "description", "teamId", "deadline", "startDate", "endDate", "status"};
            CsvHelper.writeBeansToCsv(csvProperties.getProjectsFilepath(), Project.class, projects, columns);
            logger.info("Project saved successfully: {}", project);
            return project;
        } catch (Exception e) {
            logger.error("Error saving project to CSV", e);
            throw new RuntimeException("Error saving project to CSV", e);
        }
    }

    /**
     * Retrieves all projects from the CSV file.
     *
     * @return a list of {@code Project} objects
     */
    @Override
    public List<Project> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getProjectsFilepath()))) {
            ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Project.class);
            String[] memberFieldsToBindTo = {"id", "name", "description", "teamId", "deadline", "startDate", "endDate", "status"};
            strategy.setColumnMapping(memberFieldsToBindTo);
            return new CsvToBeanBuilder<Project>(reader)
                .withMappingStrategy(strategy)
                .build()
                .parse();
        } catch (IOException e) {
            logger.error("Error reading projects from CSV", e);
            throw new RuntimeException("Error reading projects from CSV", e);
        }
    }

    /**
     * Finds a project by its ID.
     *
     * @param id the ID of the project
     * @return an {@code Optional} containing the project if found, or empty if not found
     */
    @Override
    public Optional<Project> findById(UUID id) {
        return findAll().stream()
            .filter(p -> Objects.equals(p.getId(), id))
            .findFirst();
    }

    /**
     * Deletes a project by its ID.
     *
     * @param id the ID of the project to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(UUID id) {
        try {
            CsvFileHelper.backupCSVFile(csvProperties.getProjectsFilepath());
            List<Project> projects = findAll();
            projects.removeIf(p -> Objects.equals(p.getId(), id));
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getProjectsFilepath()))) {
                ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Project.class);
                String[] memberFieldsToBindTo = {"id", "name", "description", "teamId", "deadline", "startDate", "endDate", "status"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Project> beanToCsv = new StatefulBeanToCsvBuilder<Project>(writer)
                    .withMappingStrategy(strategy)
                    .build();
                beanToCsv.write(projects);
            }
            logger.info("Project deleted successfully: {}", id);
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException |
                 com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting project from CSV", e);
            throw new RuntimeException("Error deleting project from CSV", e);
        }
    }

    /**
     * Finds projects by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of {@code Project} objects belonging to the team
     */
    @Override
    public List<Project> findAllByTeamId(UUID teamId) {
        return findAll().stream()
            .filter(p -> p.getTeam() != null && Objects.equals(p.getTeam().getId(), teamId))
            .toList();
    }
}
