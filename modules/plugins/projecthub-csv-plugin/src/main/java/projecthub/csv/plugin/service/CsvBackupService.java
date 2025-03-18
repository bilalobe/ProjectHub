package projecthub.csv.plugin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import projecthub.csv.plugin.exception.CsvDataAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CsvBackupService {
    private static final Logger logger = LoggerFactory.getLogger(CsvBackupService.class);
    private static final String BACKUP_DIR = "backups";
    private static final DateTimeFormatter BACKUP_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public CsvBackupService() {
    }

    public void createBackup(String filePath) {
        Path source = Paths.get(filePath);
        if (!Files.exists(source)) {
            logger.warn("Source file does not exist: {}", filePath);
            return;
        }

        try {
            Path backupDir = source.getParent().resolve(BACKUP_DIR);
            Files.createDirectories(backupDir);

            String timestamp = LocalDateTime.now().format(BACKUP_TIMESTAMP);
            String fileName = source.getFileName().toString();
            String backupFileName = fileName.replace(".csv", "_" + timestamp + ".csv");
            Path backupPath = backupDir.resolve(backupFileName);

            Files.copy(source, backupPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Created backup of {} at {}", filePath, backupPath);

            cleanOldBackups(backupDir, fileName);
        } catch (IOException e) {
            throw new CsvDataAccessException("Failed to create backup for " + filePath, e);
        }
    }

    private static void cleanOldBackups(Path backupDir, String originalFileName) {
        try {
            String baseFileName = originalFileName.replace(".csv", "");
            Files.list(backupDir)
                .filter(path -> path.getFileName().toString().startsWith(baseFileName))
                .sorted((p1, p2) -> -p1.getFileName().toString().compareTo(p2.getFileName().toString()))
                .skip(5L) // Keep last 5 backups
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        logger.debug("Deleted old backup: {}", path);
                    } catch (IOException e) {
                        logger.warn("Failed to delete old backup: {}", path, e);
                    }
                });
        } catch (IOException e) {
            logger.warn("Failed to clean old backups in {}", backupDir, e);
        }
    }
}
