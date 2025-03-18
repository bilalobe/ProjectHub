package com.projecthub.api.csv

import org.koin.dsl.module

/**
 * Koin module that provides CSV export functionality dependencies
 */
val csvModule = module {
    // Provide the bridge between Ktor and Java CSV plugin
    single { JavaCsvPluginBridge() }
}
