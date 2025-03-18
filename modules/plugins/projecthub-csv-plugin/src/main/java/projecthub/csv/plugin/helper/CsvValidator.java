package projecthub.csv.plugin.helper;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import projecthub.csv.plugin.repository.CsvRepositoryException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class CsvValidator {
    private static final Logger logger = LoggerFactory.getLogger(CsvValidator.class);

    public CsvValidator() {
    }

    public void validateCsvStructure(String filepath, String[] expectedColumns) {
        try (CSVReader reader = new CSVReader(new FileReader(filepath, StandardCharsets.UTF_8))) {
            String[] headers = reader.readNext();
            if (headers == null) {
                throw new CsvValidationException("CSV file is empty");
            }

            validateHeaders(headers, expectedColumns);
            validateRows(reader, headers.length);

            logger.debug("CSV validation successful for file: {}", filepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new CsvRepositoryException("CSV validation failed for file: " + filepath, e);
        }
    }

    private static void validateHeaders(String[] actual, String[] expected) throws CsvValidationException {
        Collection<String> actualSet = new HashSet<>(Arrays.asList(actual));
        Set<String> expectedSet = new HashSet<>(Arrays.asList(expected));

        Collection<String> missing = new HashSet<>(expectedSet);
        missing.removeAll(actualSet);

        if (!missing.isEmpty()) {
            throw new CsvValidationException("Missing required columns: " + String.join(", ", missing));
        }
    }

    private static void validateRows(CSVReader reader, int expectedColumns) throws IOException, CsvValidationException {
        String[] row;
        int rowNum = 1;
        while ((row = reader.readNext()) != null) {
            rowNum++;
            if (row.length != expectedColumns) {
                throw new CsvValidationException(
                    String.format("Invalid number of columns at row %d. Expected: %d, Found: %d",
                        Integer.valueOf(rowNum), Integer.valueOf(expectedColumns), Integer.valueOf(row.length)));
            }
        }
    }

    public void validateUniqueConstraint(String filepath, int... columnIndices) {
        try (CSVReader reader = new CSVReader(new FileReader(filepath, StandardCharsets.UTF_8))) {
            reader.readNext(); // Skip headers
            Collection<String> seen = new HashSet<>();
            String[] row;
            int rowNum = 1;

            while ((row = reader.readNext()) != null) {
                rowNum++;
                String key = buildUniqueKey(row, columnIndices);
                if (!seen.add(key)) {
                    throw new CsvValidationException(
                        String.format("Duplicate entry found at row %d for columns %s",
                                Integer.valueOf(rowNum), Arrays.toString(columnIndices)));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new CsvRepositoryException("Unique constraint validation failed for file: " + filepath, e);
        }
    }

    private static String buildUniqueKey(String[] row, int[] columnIndices) {
        StringBuilder key = new StringBuilder();
        for (int index : columnIndices) {
            if (index >= row.length) {
                throw new IllegalArgumentException("Column index out of bounds: " + index);
            }
            key.append(row[index]).append('|');
        }
        return key.toString();
    }
}
