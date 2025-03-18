package projecthub.csv.plugin.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

@Component
public class CsvTransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(CsvTransactionManager.class);
    private final ThreadLocal<Stack<String>> transactionBackups = ThreadLocal.withInitial(Stack::new);
    private final ThreadLocal<Map<String, String>> fileBackups = ThreadLocal.withInitial(HashMap::new);

    public CsvTransactionManager() {
    }

    public String beginTransaction(String... filePaths) {
        String transactionId = UUID.randomUUID().toString();
        try {
            for (String filePath : filePaths) {
                String backupPath = createBackup(filePath, transactionId);
                fileBackups.get().put(filePath, backupPath);
            }
            transactionBackups.get().push(transactionId);
            logger.debug("Started transaction: {}", transactionId);
            return transactionId;
        } catch (Exception e) {
            rollback();
            throw new RuntimeException("Failed to start transaction", e);
        }
    }

    public void commit() {
        if (transactionBackups.get().isEmpty()) {
            throw new IllegalStateException("No active transaction");
        }
        String transactionId = transactionBackups.get().pop();
        cleanup(transactionId);
        logger.debug("Committed transaction: {}", transactionId);
    }

    public void rollback() {
        if (transactionBackups.get().isEmpty()) {
            throw new IllegalStateException("No active transaction");
        }

        String transactionId = transactionBackups.get().pop();
        try {
            Map<String, String> backups = fileBackups.get();
            for (Map.Entry<String, String> entry : backups.entrySet()) {
                Files.copy(Path.of(entry.getValue()),
                         Path.of(entry.getKey()),
                         StandardCopyOption.REPLACE_EXISTING);
            }
            cleanup(transactionId);
            logger.debug("Rolled back transaction: {}", transactionId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("Failed to rollback transaction: {}", transactionId, e);
            throw new RuntimeException("Failed to rollback transaction", e);
        }
    }

    private static String createBackup(String filePath, String transactionId) throws IOException {
        String backupPath = filePath + "." + transactionId + ".bak";
        Files.copy(Path.of(filePath), Path.of(backupPath), StandardCopyOption.REPLACE_EXISTING);
        return backupPath;
    }

    private void cleanup(String transactionId) {
        try {
            Map<String, String> backups = fileBackups.get();
            for (String backupPath : backups.values()) {
                Files.deleteIfExists(Path.of(backupPath));
            }
            backups.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.warn("Failed to cleanup transaction backups: {}", transactionId, e);
        }
    }
}
