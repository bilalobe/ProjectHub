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
    public CohortDTO getCohort(@InputArgument final String id) {
        return this.queryService.findById(UUID.fromString(id));
    }

    @DgsQuery(field = "cohorts")
    public CohortConnection getCohorts(
        @InputArgument final Integer first,
        @InputArgument final String after,
        @InputArgument final Integer last,
        @InputArgument final String before
    ) {
        return this.queryService.findAll(first, after, last, before);
    }

    @DgsQuery(field = "cohortsBySchool")
    public List<CohortDTO> getCohortsBySchool(@InputArgument final String schoolId) {
        return this.queryService.findBySchoolId(UUID.fromString(schoolId));
    }
}
