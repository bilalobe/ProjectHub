package com.projecthub.base.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Detailed validation error.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ApiValidationError extends ApiSubError {
    private String field;
    private Object rejectedValue;
    private String message;
}
