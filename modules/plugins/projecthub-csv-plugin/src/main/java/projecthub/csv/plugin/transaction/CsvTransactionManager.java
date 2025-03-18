package projecthub.csv.plugin.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import projecthub.csv.plugin.exception.CsvDataAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class CsvTransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(CsvTransactionManager.class);
    private final Map<String, ReentrantLock> fileLocks = new ConcurrentHashMap<>();
    private final ThreadLocal<Map<String, Path>> activeTransactions = ThreadLocal.withInitial(HashMap::new);

    public CsvTransactionManager() {
    }

    public void beginTransaction(String... filePaths) {
        for (String filePath : filePaths) {
            ReentrantLock lock = fileLocks.computeIfAbsent(filePath, k -> new ReentrantLock());
            if (!lock.tryLock()) {
                rollbackTransaction();
                throw new CsvDataAccessException("Could not acquire lock for file: " + filePath);
            }

            try {
                Path backup = createBackup(filePath);
                activeTransactions.get().put(filePath, backup);
            } catch (IOException e) {
                rollbackTransaction();
                throw new CsvDataAccessException("Failed to create backup for transaction", e);
            }
        }
    }

    public void commitTransaction() {
        Map<String, Path> transaction = activeTransactions.get();
        try {
            for (Path backup : transaction.values()) {
                Files.deleteIfExists(backup);
            }
        } catch (IOException e) {
            logger.warn("Failed to clean up transaction backups", e);
        } finally {
            releaseAllLocks();
            activeTransactions.remove();
        }
    }

    public void rollbackTransaction() {
        Map<String, Path> transaction = activeTransactions.get();
        for (Map.Entry<String, Path> entry : transaction.entrySet()) {
            try {
                Path original = Paths.get(entry.getKey());
                Path backup = entry.getValue();
                Files.move(backup, original, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                logger.error("Failed to rollback changes for file: {}", entry.getKey(), e);
            }
        }
        releaseAllLocks();
        activeTransactions.remove();
    }

    private static Path createBackup(String filePath) throws IOException {
        Path original = Paths.get(filePath);
        Path backup = Paths.get(filePath + ".bak");
        Files.copy(original, backup, StandardCopyOption.REPLACE_EXISTING);
        return backup;
    }

    private void releaseAllLocks() {
        activeTransactions.get().keySet().forEach(filePath -> {
            ReentrantLock lock = fileLocks.get(filePath);
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        });
    }
}
