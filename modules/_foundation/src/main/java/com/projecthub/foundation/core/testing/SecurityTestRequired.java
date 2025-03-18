package com.projecthub.core.testing;

public interface SecurityTestRequired {
    String[] getRequiredSecurityTests();
    String[] getSecurityPrinciples();
    boolean isSecurityCritical();
}