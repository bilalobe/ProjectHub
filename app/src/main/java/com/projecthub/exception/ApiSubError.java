package com.projecthub.exception;

import lombok.Data;

/**
 * Base class for sub-errors used in ApiError.
 */
@Data

public abstract class ApiSubError {
    // Can be extended for different types of sub-errors
}