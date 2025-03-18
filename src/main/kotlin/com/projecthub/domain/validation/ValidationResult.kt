package com.projecthub.domain.validation

/**
 * Represents the result of a validation operation.
 * Contains information about whether the validation passed and any error messages.
 */
class ValidationResult private constructor(
    val isValid: Boolean,
    val errors: List<ValidationError> = emptyList()
) {
    companion object {
        /**
         * Create a successful validation result.
         */
        fun success(): ValidationResult = ValidationResult(true)

        /**
         * Create a failed validation result with the specified errors.
         */
        fun failure(vararg errors: ValidationError): ValidationResult =
            ValidationResult(false, errors.toList())

        /**
         * Create a failed validation result with the specified error messages.
         */
        fun failure(vararg errorMessages: String): ValidationResult =
            ValidationResult(false, errorMessages.map { ValidationError(it) })
    }

    /**
     * Combines this validation result with another one.
     * The combined result is valid only if both results are valid.
     */
    fun and(other: ValidationResult): ValidationResult {
        val combinedErrors = this.errors + other.errors
        return ValidationResult(this.isValid && other.isValid, combinedErrors)
    }
}

/**
 * Represents a validation error.
 */
data class ValidationError(
    val message: String,
    val field: String? = null,
    val code: String? = null
)
