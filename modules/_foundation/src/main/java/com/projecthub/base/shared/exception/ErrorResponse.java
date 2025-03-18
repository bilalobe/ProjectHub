package com.projecthub.base.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String code,
    String message,
    String detail,
    UUID requestId,
    List<String> errors
) {
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

    public static class ErrorResponseBuilder {
        private LocalDateTime timestamp = LocalDateTime.now();
        private int status = 0;
        private String code = null;
        private String message = null;
        private String detail = null;
        private UUID requestId = UUID.randomUUID();
        private List<String> errors = new ArrayList<>();

        public ErrorResponseBuilder() {
        }

        public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ErrorResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ErrorResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorResponseBuilder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public ErrorResponseBuilder requestId(UUID requestId) {
            this.requestId = requestId;
            return this;
        }

        public ErrorResponseBuilder error(String error) {
            this.errors.add(error);
            return this;
        }

        public ErrorResponseBuilder errors(Collection<String> errors) {
            this.errors.addAll(errors);
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(timestamp, status, code, message, detail, requestId, errors);
        }
    }
}
