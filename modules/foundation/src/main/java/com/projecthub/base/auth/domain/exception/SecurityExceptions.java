package com.projecthub.base.auth.domain.exception;

public class SecurityExceptions {

    public class MfaRequiredException extends SecurityException {
        public MfaRequiredException(String message) {
            super(message);
        }
    }

    public class SuspiciousLocationException extends SecurityException {
        public SuspiciousLocationException(String message) {
            super(message);
        }
    }

    public class TokenValidationException extends SecurityException {
        public TokenValidationException(String message) {
            super(message);
        }
    }

    public class AuthenticationLimitExceededException extends SecurityException {
        public AuthenticationLimitExceededException(String message) {
            super(message);
        }
    }

    public class SecurityPolicyViolationException extends SecurityException {
        public SecurityPolicyViolationException(String message) {
            super(message);
        }
    }
}
