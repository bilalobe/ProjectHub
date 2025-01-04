package com.projecthub.base.cohort.application.exception;

import com.netflix.graphql.dgs.exceptions.DefaultDataFetcherExceptionHandler;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import com.projecthub.base.cohort.api.exception.GraphQLApiException;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class CohortExceptionHandler implements DataFetcherExceptionHandler {
    private final DefaultDataFetcherExceptionHandler defaultHandler = new DefaultDataFetcherExceptionHandler();

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(
        DataFetcherExceptionHandlerParameters handlerParameters
    ) {
        Throwable exception = handlerParameters.getException();

        if (exception instanceof GraphQLApiException) {
            return CompletableFuture.completedFuture(
                DataFetcherExceptionHandlerResult.newResult()
                    .error(TypedGraphQLError.newBuilder()
                        .message(exception.getMessage())
                        .path(handlerParameters.getPath())
                        .build())
                    .build()
            );
        }

        if (exception instanceof ConstraintViolationException e) {
            return CompletableFuture.completedFuture(
                DataFetcherExceptionHandlerResult.newResult()
                    .error(TypedGraphQLError.newBuilder()
                        .message("Validation failed: " + e.getMessage())
                        .path(handlerParameters.getPath())
                        .build())
                    .build()
            );
        }

        return defaultHandler.handleException(handlerParameters);
    }
}
