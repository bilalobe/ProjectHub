package projecthub.csv.plugin.repository.impl;

@Repository("csvStudentRepository")
@Profile("csv")
public class StudentCsvRepositoryImpl implements StudentCsvRepository {

    private static final Logger logger = LoggerFactory.getLogger(StudentCsvRepositoryImpl.class);

    private final Validator validator;
    private final CsvProperties csvProperties;

    public StudentCsvRepositoryImpl(CsvProperties csvProperties, Validator validator) {
        this.csvProperties = csvProperties;
        this.validator = validator;
    }

    /**
     * Validates a {@link Student} object.
     *
     * @param student the {@code Student} object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateStudent(Student student) {
        Set<ConstraintViolation<Student>> violations = validator.validate(student);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Student> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Student validation failed: " + sb);
        }
    }

    /**
     * Saves a student to the CSV file after validation and backup.
     *
     * @param student the {@code Student} object to save
     * @return the saved {@code Student} object
     * @throws RuntimeException if an error occurs during saving
     */
    @Override
    public Student save(Student student) {
        validateStudent(student);
        try {
            CsvFileHelper.backupCSVFile(csvProperties.getStudentsFilepath());
            List<Student> students = findAll();
            students.removeIf(s -> Objects.equals(s.getId(), student.getId()));
            students.add(student);
            String[] columns = {"id", "name", "teamId"};
            CsvHelper.writeBeansToCsv(csvProperties.getStudentsFilepath(), Student.class, students, columns);
            logger.info("Student saved successfully: {}", student);
            return student;
        } catch (RuntimeException e) {
            logger.error("Error saving student to CSV", e);
            throw new RuntimeException("Error saving student to CSV", e);
        }
    }

    /**
     * Retrieves all students from the CSV file.
     *
     * @return a list of {@code Student} objects
     */
    @Override
    public List<Student> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(csvProperties.getStudentsFilepath()))) {
            ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Student.class);
            String[] memberFieldsToBindTo = {"id", "name", "teamId"};
            strategy.setColumnMapping(memberFieldsToBindTo);
            return new CsvToBeanBuilder<Student>(reader)
                .withMappingStrategy(strategy)
                .build()
                .parse();
        } catch (IOException e) {
            logger.error("Error reading students from CSV", e);
            throw new RuntimeException("Error reading students from CSV", e);
        }
    }

    /**
     * Finds a student by their ID.
     *
     * @param id the ID of the student
     * @return an {@code Optional} containing the student if found, or empty if not found
     */
    @Override
    public Optional<Student> findById(UUID id) {
        return findAll().stream()
            .filter(s -> Objects.equals(s.getId(), id))
            .findFirst();
    }

    /**
     * Deletes a student by their ID.
     *
     * @param id the ID of the student to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Override
    public void deleteById(UUID id) {
        try {
            CsvFileHelper.backupCSVFile(csvProperties.getStudentsFilepath());
            List<Student> students = findAll();
            students.removeIf(s -> Objects.equals(s.getId(), id));
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvProperties.getStudentsFilepath()))) {
                ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Student.class);
                String[] memberFieldsToBindTo = {"id", "name", "teamId"};
                strategy.setColumnMapping(memberFieldsToBindTo);
                StatefulBeanToCsv<Student> beanToCsv = new StatefulBeanToCsvBuilder<Student>(writer)
                    .withMappingStrategy(strategy)
                    .build();
                beanToCsv.write(students);
            }
            logger.info("Student deleted successfully: {}", id);
        } catch (IOException | com.opencsv.exceptions.CsvDataTypeMismatchException |
                 com.opencsv.exceptions.CsvRequiredFieldEmptyException e) {
            logger.error("Error deleting student from CSV", e);
            throw new RuntimeException("Error deleting student from CSV", e);
        }
    }

    /**
     * Finds students by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of {@code Student} objects belonging to the team
     */
    @Override
    public List<Student> findByTeamId(UUID teamId) {
        return findAll().stream()
            .filter(s -> s.getTeam() != null && Objects.equals(s.getTeam().getId(), teamId))
            .toList();
    }
}
