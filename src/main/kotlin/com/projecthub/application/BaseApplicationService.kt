package com.projecthub.application

import com.projecthub.domain.BaseAggregateRoot
import com.projecthub.events.ApplicationEventDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

/**
 * Base class for application services that provides common functionality.
 * Integrates coroutines with transaction management and event dispatching.
 */
abstract class BaseApplicationService(
    protected val transactionManager: CoroutineTransactionManager,
    protected val eventDispatcher: ApplicationEventDispatcher
) : ApplicationService {

    protected val logger = LoggerFactory.getLogger(this::class.java)
    protected val serviceScope = CoroutineScope(Dispatchers.Default)

    /**
     * Executes a use case within a transaction and dispatches any domain events.
     *
     * @param readOnly Whether the transaction should be read-only
     * @param block The suspending function to execute
     * @return The result of the block execution
     */
    protected suspend fun <T> executeUseCase(
        readOnly: Boolean = false,
        block: suspend () -> T
    ): T = transactionManager.withTransaction(readOnly = readOnly) {
        block()
    }

    /**
     * Executes a read-only use case within a transaction.
     *
     * @param block The suspending function to execute
     * @return The result of the block execution
     */
    protected suspend fun <T> executeReadOnly(
        block: suspend () -> T
    ): T = executeUseCase(readOnly = true, block = block)

    /**
     * Dispatches events from an aggregate root within the current transaction.
     *
     * @param aggregate The aggregate root containing events to dispatch
     */
    protected suspend fun dispatchEventsFrom(aggregate: BaseAggregateRoot) {
        withContext(Dispatchers.IO) {
            eventDispatcher.dispatchEventsFrom(aggregate)
        }
    }
}
