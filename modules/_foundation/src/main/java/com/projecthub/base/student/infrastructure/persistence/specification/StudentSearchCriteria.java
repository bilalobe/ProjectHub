package com.projecthub.base.student.infrastructure.persistence.specification;

import com.projecthub.base.shared.domain.enums.status.ActivationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class StudentSearchCriteria {
    private String nameSearch;
    private String email;
    private ActivationStatus status;  // Changed back to ActivationStatus type
    private UUID teamId;
    private LocalDate enrollmentDateStart;
    private LocalDate enrollmentDateEnd;
}