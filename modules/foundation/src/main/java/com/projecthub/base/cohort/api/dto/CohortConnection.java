package com.projecthub.base.cohort.api.dto;

import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

@Value
public class CohortConnection {
    List<CohortEdge> edges;
    PageInfo pageInfo;
    long totalCount;

    public static CohortConnection of(Page<CohortDTO> page) {
        List<CohortEdge> edges = page.getContent().stream()
            .map(CohortEdge::new)
            .toList();

        return new CohortConnection(
            edges,
            new PageInfo(
                page.hasNext(),
                page.hasPrevious(),
                page.getTotalElements()
            ),
            page.getTotalElements()
        );
    }
}
