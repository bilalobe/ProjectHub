package com.projecthub.base.student.api.dto;

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
    private ActivationStatus status;
    private UUID teamId;
    private LocalDate enrollmentDateStart;
    private LocalDate enrollmentDateEnd;
}