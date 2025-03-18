package projecthub.csv.plugin.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import projecthub.csv.plugin.repository.CsvRepositoryException;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CsvFileHelper {
    private static final Logger logger = LoggerFactory.getLogger(CsvFileHelper.class);
    private static final DateTimeFormatter BACKUP_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String BACKUP_DIR = "backups";

    public CsvFileHelper() {
    }

    public static void ensureFileExists(String filepath) {
        try {
            Path path = Paths.get(filepath);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                logger.info("Created new CSV file: {}", filepath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new CsvRepositoryException("Failed to ensure CSV file exists: " + filepath, e);
        }
    }

    public void backupCSVFile(String filepath) {
        try {
            Path source = Paths.get(filepath);
            if (!Files.exists(source)) {
                return;
            }

            Path backupDir = source.getParent().resolve(BACKUP_DIR);
            Files.createDirectories(backupDir);

            String timestamp = LocalDateTime.now().format(BACKUP_TIME_FORMAT);
            String fileName = source.getFileName().toString();
            Path backup = backupDir.resolve(fileName + "." + timestamp + ".bak");

            Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
            logger.debug("Created backup of CSV file: {}", backup);

            // Clean up old backups (keep last 5)
            cleanupOldBackups(backupDir, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new CsvRepositoryException("Failed to backup CSV file: " + filepath, e);
        }
    }

    private static void cleanupOldBackups(Path backupDir, String fileName) {
        try {
            Files.list(backupDir)
                .filter(p -> p.getFileName().toString().startsWith(fileName + "."))
                .sorted()
                .limit(Math.max(0L, Files.list(backupDir).count() - 5L))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                        logger.debug("Deleted old backup: {}", p);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        logger.warn("Failed to delete old backup: {}", p, e);
                    }
                });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.warn("Failed to cleanup old backups in directory: {}", backupDir, e);
        }
    }

    public boolean isFileLocked(String filepath) {
        try {
            Path path = Paths.get(filepath);
            if (!Files.exists(path)) {
                return false;
            }

            try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
                return channel.tryLock() == null;
            }
        } catch (RuntimeException e) {
            logger.warn("Error checking if file is locked: {}", filepath, e);
            return true;
        }
    }

    public void waitForFileUnlock(String filepath, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMillis = (long) timeoutSeconds * 1000L;

        while (isFileLocked(filepath)) {
            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                throw new CsvRepositoryException("Timeout waiting for file to unlock: " + filepath);
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CsvRepositoryException("Interrupted while waiting for file to unlock", e);
            }
        }
    }
}
