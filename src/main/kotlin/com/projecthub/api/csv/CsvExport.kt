package com.projecthub.api.csv

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.*
import org.koin.ktor.ext.inject

/**
 * CsvExport feature for Ktor that simplifies responding with CSV data.
 * This feature provides extension methods to ApplicationCall to easily
 * send CSV responses with proper headers and formatting.
 */
class CsvExport(configuration: Configuration) {
    private val csvBridge = configuration.csvBridge

    /**
     * Configuration for the CsvExport feature
     */
    class Configuration {
        internal var csvBridge: JavaCsvPluginBridge? = null

        /**
         * Set the CSV plugin bridge to use
         */
        fun setCsvBridge(bridge: JavaCsvPluginBridge) {
            this.csvBridge = bridge
        }
    }

    /**
     * Respond with CSV data using the provided headers and filename
     */
    suspend fun respondCSV(call: ApplicationCall, filename: String, headers: List<String>, data: List<List<Any?>>) {
        val csvBridge = this.csvBridge ?: throw IllegalStateException("CSV Bridge not configured")

        csvBridge.apply {
            call.respondWithCsvDownload(
                data = data.map { row -> row.map { it?.toString() ?: "" } },
                headers = headers,
                fileName = filename
            )
        }
    }

    /**
     * Companion object that provides the install function
     */
    companion object Plugin : ApplicationPlugin<ApplicationCallPipeline, CsvExport.Configuration, CsvExport> {
        override val key = AttributeKey<CsvExport>("CsvExport")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit
        ): CsvExport {
            val configuration = Configuration().apply(configure)

            if (configuration.csvBridge == null) {
                // Try to get the bridge from Koin if not explicitly set
                val application = pipeline as? Application
                if (application != null) {
                    val csvBridge: JavaCsvPluginBridge = application.inject<JavaCsvPluginBridge>().value
                    configuration.csvBridge = csvBridge
                }
            }

            val feature = CsvExport(configuration)

            // Store the feature in the pipeline attributes
            pipeline.attributes.put(key, feature)

            return feature
        }
    }
}

/**
 * Access the CsvExport feature from an ApplicationCall
 */
val ApplicationCall.csvExport: CsvExport
    get() =
        application.attributes[CsvExport.Plugin.key]

/**
 * Configure the CSV export feature in the application
 */
fun Application.configureCSVExport() {
    install(CsvExport) {
        // Configuration will be automatically set via Koin injection
    }
}
