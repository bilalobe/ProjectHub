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
    public SchoolDTO school(@InputArgument String id) {
        return schoolQuery.getSchoolById(UUID.fromString(id));
    }

    @DgsQuery
    public SchoolConnection schools(@InputArgument Integer page, @InputArgument Integer size) {
        Page<SchoolDTO> schoolPage = schoolQuery.getAllSchools(
            PageRequest.of(page != null ? page : 0, size != null ? size : 10)
        );
        return SchoolConnection.from(schoolPage);
    }

    @DgsQuery
    public SchoolConnection searchSchools(
        @InputArgument SchoolSearchInput criteria,
        @InputArgument Integer page,
        @InputArgument Integer size) {
        return SchoolConnection.from(schoolQuery.searchSchools(
            toSearchCriteria(criteria),
            PageRequest.of(page != null ? page : 0, size != null ? size : 10)
        ));
    }

    @DgsQuery
    public SchoolConnection activeSchools(@InputArgument Integer page, @InputArgument Integer size) {
        return SchoolConnection.from(schoolQuery.getActiveSchools(
            PageRequest.of(page != null ? page : 0, size != null ? size : 10)
        ));
    }

    @DgsQuery
    public SchoolConnection archivedSchools(@InputArgument Integer page, @InputArgument Integer size) {
        return SchoolConnection.from(schoolQuery.getArchivedSchools(
            PageRequest.of(page != null ? page : 0, size != null ? size : 10)
        ));
    }

    private SchoolSearchCriteria toSearchCriteria(SchoolSearchInput input) {
        return SchoolSearchCriteria.builder()
            .name(input.getName())
            .city(input.getCity())
            .state(input.getState())
            .archived(input.getArchived())
            .build();
    }
}
