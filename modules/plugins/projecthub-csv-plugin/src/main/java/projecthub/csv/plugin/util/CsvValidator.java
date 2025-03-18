package projecthub.csv.plugin.util;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;
import projecthub.csv.plugin.exception.CsvDataAccessException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class CsvValidator {

    public CsvValidator() {
    }

    public static void validateHeaders(String filePath, String[] expectedHeaders) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String[] actualHeaders = reader.readNext();
            if (actualHeaders == null) {
                throw new CsvDataAccessException("CSV file is empty: " + filePath);
            }

            List<String> expected = Arrays.asList(expectedHeaders);
            List<String> actual = Arrays.asList(actualHeaders);

            if (!actual.equals(expected)) {
                throw new CsvDataAccessException(String.format(
                    "CSV headers mismatch in %s. Expected: %s, Found: %s",
                    filePath, expected, actual
                ));
            }
        } catch (IOException e) {
            throw new CsvDataAccessException("Error validating CSV headers: " + filePath, e);
        }
    }

    public static void validateFileExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new CsvDataAccessException("CSV file does not exist: " + filePath);
        }
        if (!file.canRead() || !file.canWrite()) {
            throw new CsvDataAccessException("CSV file is not accessible: " + filePath);
        }
    }

    public static void validateRow(String[] row, int expectedColumns, String filePath, int lineNumber) {
        if (row == null || row.length != expectedColumns) {
            throw new CsvDataAccessException(String.format(
                "Invalid row in %s at line %d. Expected %d columns, found %d",
                filePath, Integer.valueOf(lineNumber), Integer.valueOf(expectedColumns), Integer.valueOf(row == null ? 0 : row.length)
            ));
        }
    }
}
