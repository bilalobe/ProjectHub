package projecthub.csv.plugin.service;

import org.springframework.stereotype.Service;
import projecthub.csv.plugin.helper.CsvHelper;
import projecthub.csv.plugin.repository.CsvRepositoryException;

@Service
public class CsvTransactionService {
    private final CsvHelper csvHelper;

    public CsvTransactionService(CsvHelper csvHelper) {
        this.csvHelper = csvHelper;
    }

    public <T> T executeInTransaction(CsvOperation<T> operation, String... filePaths) {
        String transactionId = csvHelper.beginTransaction(filePaths);
        try {
            T result = operation.execute();
            csvHelper.commitTransaction();
            return result;
        } catch (Exception e) {
            csvHelper.rollbackTransaction();
            throw new CsvRepositoryException("Transaction failed", e);
        }
    }

    @FunctionalInterface
    public interface CsvOperation<T> {
        T execute() throws Exception;
    }
}