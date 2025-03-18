package com.projecthub.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors

/**
 * Configuration for coroutines in the application.
 * Provides customized dispatchers for different use cases.
 */
@Configuration
class CoroutineConfig {

    /**
     * Provides a dispatcher for IO-bound operations, like database access.
     */
    @Bean(name = ["ioDispatcher"])
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides a dispatcher for CPU-bound operations, like calculations.
     */
    @Bean(name = ["computationDispatcher"])
    fun computationDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Provides a dispatcher for database operations with a fixed thread pool.
     * This can be useful for controlling the number of concurrent database connections.
     */
    @Bean(name = ["databaseDispatcher"])
    fun databaseDispatcher(): CoroutineDispatcher =
        Executors.newFixedThreadPool(10).asCoroutineDispatcher()

    /**
     * Provides a dispatcher for API operations.
     */
    @Bean(name = ["apiDispatcher"])
    fun apiDispatcher(): CoroutineDispatcher =
        Executors.newFixedThreadPool(20).asCoroutineDispatcher()
}
