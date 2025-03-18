package com.projecthub.api.csv

import io.ktor.server.application.*
import org.slf4j.LoggerFactory

/**
 * Configuration for the CSV export functionality
 */
class CsvExportConfig {
    /**
     * Default separator character for CSV files (default is comma)
     */
    var separator: Char = ','

    /**
     * Default character encoding for CSV files (default is UTF-8)
     */
    var encoding: String = "UTF-8"

    /**
     * Default line ending for CSV files (default is \r\n)
     */
    var lineEnding: String = "\r\n"

    /**
     * Whether to include headers in the CSV file (default is true)
     */
    var includeHeaders: Boolean = true

    /**
     * Default quote character for CSV fields (default is double quote)
     */
    var quoteChar: Char = '"'

    /**
     * Maximum number of records to export in a single request (default is 10000)
     */
    var maxRecords: Int = 10000

    /**
     * Whether to add BOM (Byte Order Mark) for UTF-8 encoding (helpful for Excel)
     */
    var addBom: Boolean = false

    /**
     * Whether to create a backup of the file being imported before processing
     */
    var createBackupBeforeImport: Boolean = true

    /**
     * Directory for storing temporary files during import/export operations
     */
    var tempDir: String = System.getProperty("java.io.tmpdir")

    /**
     * Directory for storing audit logs of export operations
     */
    var auditLogDir: String? = null

    /**
     * Enable detailed logging of CSV operations
     */
    var detailedLogging: Boolean = true

    /**
     * Default logger for CSV operations
     */
    var logger = LoggerFactory.getLogger("com.projecthub.csv")
}

/**
 * Configure the CSV export in the application
 */
fun Application.configureCsvExport(block: CsvExportConfig.() -> Unit = {}) {
    val config = CsvExportConfig().apply(block)

    // Register the config as an attribute so it can be accessed elsewhere
    attributes.put(CsvExportConfigKey, config)

    if (config.detailedLogging) {
        log.info("CSV Export configured with separator='\', encoding='\'")
    }
}

// Key to access the config in the application attributes
val CsvExportConfigKey = AttributeKey<CsvExportConfig>("CsvExportConfig")

// Extension property to get the CSV export config from the application
val Application.csvExportConfig: CsvExportConfig
    get() = attributes[CsvExportConfigKey]
