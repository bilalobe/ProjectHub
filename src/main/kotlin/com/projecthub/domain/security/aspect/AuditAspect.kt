package com.projecthub.domain.security.aspect

import com.projecthub.domain.security.audit.SecurityAuditService
import com.projecthub.domain.security.audit.SecurityAuditEvent
import com.projecthub.domain.security.audit.SecurityEventOutcome
import com.projecthub.domain.security.audit.SecurityEventType
import com.projecthub.domain.security.context.SecurityContextHolder
import kotlinx.coroutines.runBlocking
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Aspect that provides method-level auditing using Spring AOP and the Auditable annotation.
 * This captures method calls, parameters, and outcomes for security auditing purposes.
 */
@Aspect
@Component
class AuditAspect(
    private val securityAuditService: SecurityAuditService,
    private val securityContextHolder: SecurityContextHolder
) {
    private val logger = LoggerFactory.getLogger(AuditAspect::class.java)

    /**
     * Around advice that intercepts methods annotated with @Auditable
     * and logs them as security audit events.
     */
    @Around("@annotation(com.projecthub.domain.security.aspect.Auditable)")
    fun auditMethodCall(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val annotation = method.getAnnotation(Auditable::class.java)

        val startTime = System.currentTimeMillis()
        val currentUser = securityContextHolder.getCurrentUser()

        try {
            // Execute the method
            val result = joinPoint.proceed()

            // Log successful execution
            runBlocking {
                securityAuditService.recordEvent(
                    SecurityAuditEvent(
                        type = annotation.type,
                        outcome = SecurityEventOutcome.SUCCESS,
                        userId = currentUser?.id,
                        timestamp = Instant.now(),
                        details = buildAuditDetails(
                            joinPoint = joinPoint,
                            annotation = annotation,
                            elapsedTimeMs = System.currentTimeMillis() - startTime
                        )
                    )
                )
            }

            return result
        } catch (ex: Exception) {
            // Log failed execution
            runBlocking {
                securityAuditService.recordEvent(
                    SecurityAuditEvent(
                        type = annotation.type,
                        outcome = SecurityEventOutcome.FAILURE,
                        userId = currentUser?.id,
                        timestamp = Instant.now(),
                        details = buildAuditDetails(
                            joinPoint = joinPoint,
                            annotation = annotation,
                            elapsedTimeMs = System.currentTimeMillis() - startTime,
                            exception = ex
                        )
                    )
                )
            }

            // Re-throw the exception
            throw ex
        }
    }

    /**
     * Builds a detailed description of the method call for the audit log.
     */
    private fun buildAuditDetails(
        joinPoint: ProceedingJoinPoint,
        annotation: Auditable,
        elapsedTimeMs: Long,
        exception: Exception? = null
    ): String {
        val methodName = joinPoint.signature.name
        val className = joinPoint.target::class.java.simpleName

        val sb = StringBuilder()
        sb.append("Method: $className.$methodName")

        // Add custom message if provided
        if (annotation.message.isNotEmpty()) {
            sb.append(" - ${annotation.message}")
        }

        // Add execution time
        sb.append(" (execution time: $elapsedTimeMs ms)")

        // Add exception details if any
        exception?.let {
            sb.append(" - Failed with exception: ${it.javaClass.simpleName}: ${it.message}")
        }

        return sb.toString()
    }
}
