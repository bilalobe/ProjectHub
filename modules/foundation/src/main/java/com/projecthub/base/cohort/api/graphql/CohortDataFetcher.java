package com.projecthub.base.cohort.api.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.school.api.dto.SchoolDTO;
import com.projecthub.base.team.api.dto.TeamDTO;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@DgsComponent
@RequiredArgsConstructor
public class CohortDataFetcher {

    private final DataLoader<UUID, SchoolDTO> schoolDataLoader;
    private final DataLoader<UUID, List<TeamDTO>> teamsDataLoader;

    @DgsData(parentType = "Cohort", field = "school")
    public CompletableFuture<SchoolDTO> school(final DgsDataFetchingEnvironment dfe) {
        final CohortDTO cohort = dfe.getSource();
        return this.schoolDataLoader.load(cohort.getSchoolId());
    }

    @DgsData(parentType = "Cohort", field = "teams")
    public CompletableFuture<List<TeamDTO>> teams(final DgsDataFetchingEnvironment dfe) {
        final CohortDTO cohort = dfe.getSource();
        return this.teamsDataLoader.load(cohort.getId());
    }
}
