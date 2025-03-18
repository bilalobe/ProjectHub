package projecthub.csv.plugin.repository;

public class CsvRepositoryException extends RuntimeException {
    public CsvRepositoryException(String message) {
        super(message);
    }

    public CsvRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}