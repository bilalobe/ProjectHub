package projecthub.csv.plugin.helper;

import com.opencsv.CSVWriter;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import projecthub.csv.plugin.repository.CsvRepositoryException;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvHelper {
    private static final Logger logger = LoggerFactory.getLogger(CsvHelper.class);
    private final CsvTransactionManager transactionManager;

    public CsvHelper(CsvTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public static <T> void writeBeansToCsv(String filepath, Class<T> type, List<T> beans, MappingStrategy<T> mappingStrategy) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filepath, StandardCharsets.UTF_8))) {
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                .withMappingStrategy(mappingStrategy)
                .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(true)
                .build();

            beanToCsv.write(beans);
            logger.info("{} records written to CSV file: {}", Integer.valueOf(beans.size()), filepath);
        } catch (CsvDataTypeMismatchException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("Failed to write beans to CSV file: {}", filepath, e);
            throw new CsvRepositoryException("Failed to write beans to CSV file", e);
        }
    }

    public String beginTransaction(String... filePaths) {
        return transactionManager.beginTransaction(filePaths);
    }

    public void commitTransaction() {
        transactionManager.commit();
    }

    public void rollbackTransaction() {
        transactionManager.rollback();
    }
}
