package com.projecthub.repository.csv.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;

public class CsvFileHelper {

    private static final Logger logger = LoggerFactory.getLogger(CsvFileHelper.class);

    /**
     * Creates a backup of the CSV file.
     *
     * @param filePath the path of the CSV file to back up
     * @throws IOException if an I/O error occurs during backup
     */
    public static void backupCSVFile(String filePath) throws IOException {
        Path source = Paths.get(filePath);
        Path backup = Paths.get(filePath + ".backup");
        Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created for file: {}", filePath);
    }
}