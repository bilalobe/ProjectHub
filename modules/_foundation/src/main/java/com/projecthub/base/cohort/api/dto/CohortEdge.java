package com.projecthub.base.cohort.api.dto;

import lombok.Value;

@Value
public class CohortEdge {
    CohortDTO node;

    public CohortEdge(final CohortDTO node) {
        this.node = node;
    }
}
