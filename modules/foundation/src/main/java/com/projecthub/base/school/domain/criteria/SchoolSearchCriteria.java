package com.projecthub.base.school.domain.criteria;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class SchoolSearchCriteria {
    String name;
    String city;
    String state;
    Boolean archived;
    LocalDateTime createdAfter;
    LocalDateTime createdBefore;
}
