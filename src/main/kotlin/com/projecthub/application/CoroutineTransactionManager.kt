package com.projecthub.application

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

/**
 * Utility class for executing code within transactions using coroutines.
 * This allows for seamless integration of Spring's transaction management with Kotlin coroutines.
 */
@Component
class CoroutineTransactionManager(
    private val transactionManager: PlatformTransactionManager
) {
    private val logger = LoggerFactory.getLogger(CoroutineTransactionManager::class.java)

    /**
     * Executes a suspending function within a transaction.
     *
     * @param propagationBehavior The transaction propagation behavior
     * @param readOnly Whether the transaction should be read-only
     * @param isolationLevel The transaction isolation level
     * @param block The suspending function to execute within the transaction
     * @return The result of the block execution
     */
    suspend fun <T> withTransaction(
        propagationBehavior: Int = TransactionDefinition.PROPAGATION_REQUIRED,
        readOnly: Boolean = false,
        isolationLevel: Int = TransactionDefinition.ISOLATION_DEFAULT,
        block: suspend () -> T
    ): T {
        val transactionDefinition = DefaultTransactionDefinition().apply {
            this.propagationBehavior = propagationBehavior
            this.isReadOnly = readOnly
            this.isolationLevel = isolationLevel
        }

        val transactionTemplate = TransactionTemplate(transactionManager, transactionDefinition)

        return withContext(Dispatchers.IO) {
            try {
                transactionTemplate.execute {
                    // Since the transaction template execute is not suspending,
                    // we need to run the block synchronously within this context
                    kotlinx.coroutines.runBlocking { block() }
                }!!
            } catch (e: Exception) {
                logger.error("Transaction failed", e)
                throw e
            }
        }
    }
}
