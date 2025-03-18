package projecthub.csv.plugin.exception;

public class CsvDataAccessException extends RuntimeException {
    public CsvDataAccessException(String message) {
        super(message);
    }

    public CsvDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}