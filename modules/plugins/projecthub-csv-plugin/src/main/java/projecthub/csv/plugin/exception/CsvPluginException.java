package projecthub.csv.plugin.exception;

public class CsvPluginException extends RuntimeException {
    public CsvPluginException(String message) {
        super(message);
    }

    public CsvPluginException(String message, Throwable cause) {
        super(message, cause);
    }
}

class CsvFileOperationException extends CsvPluginException {
    public CsvFileOperationException(String message) {
        super(message);
    }

    public CsvFileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

class CsvValidationException extends CsvPluginException {
    public CsvValidationException(String message) {
        super(message);
    }

    public CsvValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

class CsvMigrationException extends CsvPluginException {
    public CsvMigrationException(String message) {
        super(message);
    }

    public CsvMigrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

class CsvLockException extends CsvPluginException {
    public CsvLockException(String message) {
        super(message);
    }

    public CsvLockException(String message, Throwable cause) {
        super(message, cause);
    }
}