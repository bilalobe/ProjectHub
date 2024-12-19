package com.projecthub.ui.utils;

import com.projecthub.core.dto.CohortDTO;
import com.projecthub.core.dto.SchoolDTO;
import com.projecthub.core.dto.TeamDTO;
import com.projecthub.ui.utils.loaders.CohortLoader;
import com.projecthub.ui.utils.loaders.SchoolLoader;
import com.projecthub.ui.utils.loaders.TeamLoader;
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