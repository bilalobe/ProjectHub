package com.projecthub.base.cohort.api.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GraphQLApiException extends RuntimeException implements GraphQLError {
    private final String errorCode;
    private final Map<String, Object> extensions;

    public GraphQLApiException(final String message, final String errorCode) {
        super(message);
        this.errorCode = errorCode;
        extensions = Map.of("errorCode", errorCode);
    }

    @Override
    public List<SourceLocation> getLocations() {
        return Collections.emptyList();
    }

    @Override
    public ErrorClassification getErrorType() {
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return this.extensions;
    }
}
