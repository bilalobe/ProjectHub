package com.projecthub.base.student.domain.builder;

import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.shared.domain.enums.status.ActivationStatus;
import com.projecthub.base.student.domain.entity.Student;

import java.util.ArrayList;

protected static abstract class StudentBuilder<C extends Student, B extends StudentBuilder<C, B>>
        extends BaseEntity.BaseEntityBuilder<C, B> {

    @Builder.Default
    protected ActivationStatus status = ActivationStatus.PENDING;
    @Builder.Default
    protected List<StudentDomainEvent> events = new ArrayList<>();

    protected StudentBuilder() {
    }

    public B validateAndBuild() {
        if (firstName == null || lastName == null || email == null || team == null) {
            throw new IllegalStateException("Required fields must be set");
        }
        return self();
    }
}
