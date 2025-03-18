package com.projecthub.base.cohort.api.dto;

import lombok.Value;

@Value
public class PageInfo {
    boolean hasNextPage;
    boolean hasPreviousPage;
    long totalElements;

    public PageInfo(final boolean hasNextPage, final boolean hasPreviousPage, final long totalElements) {
        this.hasNextPage = hasNextPage;
        this.hasPreviousPage = hasPreviousPage;
        this.totalElements = totalElements;
    }
}
