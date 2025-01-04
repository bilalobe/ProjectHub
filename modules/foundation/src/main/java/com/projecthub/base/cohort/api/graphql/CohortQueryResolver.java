package com.projecthub.base.cohort.api.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.projecthub.base.cohort.api.dto.CohortConnection;
import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.cohort.application.service.CohortQueryService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class CohortQueryResolver {
    private final CohortQueryService queryService;

    @DgsQuery(field = "cohort")
    public CohortDTO getCohort(@InputArgument String id) {
        return queryService.findById(UUID.fromString(id));
    }

    @DgsQuery(field = "cohorts")
    public CohortConnection getCohorts(
        @InputArgument Integer first,
        @InputArgument String after,
        @InputArgument Integer last,
        @InputArgument String before
    ) {
        return queryService.findAll(first, after, last, before);
    }

    @DgsQuery(field = "cohortsBySchool")
    public List<CohortDTO> getCohortsBySchool(@InputArgument String schoolId) {
        return queryService.findBySchoolId(UUID.fromString(schoolId));
    }
}
