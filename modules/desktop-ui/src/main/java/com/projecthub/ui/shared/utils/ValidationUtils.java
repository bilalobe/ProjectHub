package com.projecthub.ui.shared.utils;

public class ValidationUtils {
    public static boolean isValidGrade(String grade) {
        return grade != null && grade.matches("[A-F][+-]?");
    }

    public static boolean isNotEmpty(String field) {
        return field != null && !field.trim().isEmpty();
    }
}
