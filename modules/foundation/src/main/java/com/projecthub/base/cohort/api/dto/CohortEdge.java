package com.projecthub.base.cohort.api.dto;

import lombok.Value;

@Value
public class CohortEdge {
    CohortDTO node;

    public CohortEdge(CohortDTO node) {
        this.node = node;
    }
}
