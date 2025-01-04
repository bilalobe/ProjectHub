package com.projecthub.base.school.domain.exception;

import com.projecthub.base.shared.exception.ResourceNotFoundException;

import java.util.UUID;

public class SchoolNotFoundException extends ResourceNotFoundException {
    public SchoolNotFoundException(UUID id) {
        super("School", id);
    }
}
