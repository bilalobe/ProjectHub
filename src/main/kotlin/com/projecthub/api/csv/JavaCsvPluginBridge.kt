package com.projecthub.api.csv

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import projecthub.csv.plugin.service.CsvOperationsService
import projecthub.csv.plugin.util.CsvConverter
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Bridge class that connects Ktor's CSV export functionality with the existing Java CSV plugin.
 * This provides a Kotlin-friendly interface to the Java CSV operations.
 */
class JavaCsvPluginBridge : KoinComponent {
    // Inject services from the existing Java plugin
    private val csvOperationsService: CsvOperationsService by inject()
    private val csvConverter: CsvConverter by inject()

    /**
     * Export data to CSV format using the existing Java CSV plugin
     *
     * @param data The data to export
     * @param headers The CSV headers
     * @param separator The CSV field separator
     * @return CSV formatted byte array
     */
    suspend fun <T> exportToCsv(
        data: List<T>,
        headers: List<String>,
        separator: Char = ','
    ): ByteArray = withContext(Dispatchers.IO) {
        val outputStream = ByteArrayOutputStream()
        val writer = OutputStreamWriter(outputStream, StandardCharsets.UTF_8)

        // Write headers
        writeRow(writer, headers, separator)

        // Write data rows
        data.forEach { item ->
            when (item) {
                is List<*> -> writeRow(writer, item.map { it.toString() }, separator)
                else -> {
                    // Convert bean to map using CsvConverter from Java plugin
                    val rowData = csvConverter.convertBeanToMap(item)
                    val rowValues = headers.map { rowData[it]?.toString() ?: "" }
                    writeRow(writer, rowValues, separator)
                }
            }
        }

        writer.flush()
        outputStream.toByteArray()
    }

    /**
     * Write a single row to the CSV
     */
    private fun writeRow(writer: Writer, values: List<String>, separator: Char) {
        val line = values.joinToString(separator.toString()) { value ->
            if (value.contains(separator) || value.contains("\"") || value.contains("\n")) {
                "\"\""
            } else {
                value
            }
        }
        writer.write(line)
        writer.write("\n")
    }

    /**
     * Import data from CSV
     *
     * @param csvData CSV data as byte array
     * @param type The class type to convert to
     * @param expectedHeaders The expected CSV headers
     * @return List of objects parsed from CSV
     */
    suspend fun <T> importFromCsv(
        csvData: ByteArray,
        type: Class<T>,
        expectedHeaders: Array<String>
    ): List<T> = withContext(Dispatchers.IO) {
        // Write to temporary file
        val tempFile = java.io.File.createTempFile("import_", ".csv")
        tempFile.deleteOnExit()

        tempFile.writeBytes(csvData)

        // Use the existing Java service to parse the CSV
        csvOperationsService.readAll(tempFile.absolutePath, type, expectedHeaders)
    }

    /**
     * Extension function to respond with CSV file download
     *
     * @param data The data to send as CSV
     * @param headers The CSV headers
     * @param fileName The name of the file without extension
     */
    suspend fun ApplicationCall.respondWithCsvDownload(
        data: List<Any>,
        headers: List<String>,
        fileName: String
    ) {
        val csvData = exportToCsv(data, headers)

        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"))
        val safeFileName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")

        response.headers.append(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(
                ContentDisposition.Parameters.FileName,
                "\-\.csv"
            ).toString()
        )

        respondBytes(csvData, ContentType.Text.CSV)
    }
}
