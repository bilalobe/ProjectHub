package com.projecthub.base.auth.domain.exception;

public class SecurityExceptions {

    public SecurityExceptions() {
    }

    public class MfaRequiredException extends SecurityException {
        public MfaRequiredException(final String message) {
            super(message);
        }
    }

    public class SuspiciousLocationException extends SecurityException {
        public SuspiciousLocationException(final String message) {
            super(message);
        }
    }

    public class TokenValidationException extends SecurityException {
        public TokenValidationException(final String message) {
            super(message);
        }
    }

    public class AuthenticationLimitExceededException extends SecurityException {
        public AuthenticationLimitExceededException(final String message) {
            super(message);
        }
    }

    public class SecurityPolicyViolationException extends SecurityException {
        public SecurityPolicyViolationException(final String message) {
            super(message);
        }
    }
}
