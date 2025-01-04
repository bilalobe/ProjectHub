package com.projecthub.ui.shared.utils;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.school.api.dto.SchoolDTO;
import com.projecthub.base.team.api.dto.TeamDTO;
import com.projecthub.ui.shared.utils.loaders.CohortLoader;
import com.projecthub.ui.shared.utils.loaders.SchoolLoader;
import com.projecthub.ui.shared.utils.loaders.TeamLoader;
import org.springframework.stereotype.Component;

@Component
public class LoaderFactory {

    private final SchoolLoader schoolLoader;
    private final CohortLoader cohortLoader;
    private final TeamLoader teamLoader;

    public LoaderFactory(SchoolLoader schoolLoader, CohortLoader cohortLoader, TeamLoader teamLoader) {
        this.schoolLoader = schoolLoader;
        this.cohortLoader = cohortLoader;
        this.teamLoader = teamLoader;
    }

    public TreeItemLoader getLoader(Object data) {
        if (data instanceof SchoolDTO) {
            return schoolLoader;
        } else if (data instanceof CohortDTO) {
            return cohortLoader;
        } else if (data instanceof TeamDTO) {
            return teamLoader;
        }
        return null;
    }
}
