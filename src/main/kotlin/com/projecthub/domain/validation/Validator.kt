package com.projecthub.domain.validation

/**
 * Interface for validators that check whether a given object is valid.
 */
interface Validator<T> {
    /**
     * Validate the given object and return a validation result.
     */
    fun validate(target: T): ValidationResult
}
