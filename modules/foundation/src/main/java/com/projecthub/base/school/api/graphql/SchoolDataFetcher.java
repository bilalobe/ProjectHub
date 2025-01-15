package com.projecthub.base.school.api.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.projecthub.base.school.api.dto.SchoolDTO;
import com.projecthub.base.school.application.port.in.SchoolQuery;
import com.projecthub.base.school.domain.criteria.SchoolSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class SchoolDataFetcher {

    private final SchoolQuery schoolQuery;

    @DgsQuery
    public SchoolDTO school(@InputArgument final String id) {
        return this.schoolQuery.getSchoolById(UUID.fromString(id));
    }

    @DgsQuery
    public SchoolConnection schools(@InputArgument final Integer page, @InputArgument final Integer size) {
        final Page<SchoolDTO> schoolPage = this.schoolQuery.getAllSchools(
            PageRequest.of(null != page ? page : 0, null != size ? size : 10)
        );
        return SchoolConnection.from(schoolPage);
    }

    @DgsQuery
    public SchoolConnection searchSchools(
        @InputArgument final SchoolSearchInput criteria,
        @InputArgument final Integer page,
        @InputArgument final Integer size) {
        return SchoolConnection.from(this.schoolQuery.searchSchools(
            this.toSearchCriteria(criteria),
            PageRequest.of(null != page ? page : 0, null != size ? size : 10)
        ));
    }

    @DgsQuery
    public SchoolConnection activeSchools(@InputArgument final Integer page, @InputArgument final Integer size) {
        return SchoolConnection.from(this.schoolQuery.getActiveSchools(
            PageRequest.of(null != page ? page : 0, null != size ? size : 10)
        ));
    }

    @DgsQuery
    public SchoolConnection archivedSchools(@InputArgument final Integer page, @InputArgument final Integer size) {
        return SchoolConnection.from(this.schoolQuery.getArchivedSchools(
            PageRequest.of(null != page ? page : 0, null != size ? size : 10)
        ));
    }

    private SchoolSearchCriteria toSearchCriteria(final SchoolSearchInput input) {
        return SchoolSearchCriteria.builder()
            .name(input.getName())
            .city(input.getCity())
            .state(input.getState())
            .archived(input.getArchived())
            .build();
    }
}
