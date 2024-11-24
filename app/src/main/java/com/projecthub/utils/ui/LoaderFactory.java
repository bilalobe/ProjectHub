package com.projecthub.utils.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.CohortSummary;
import com.projecthub.dto.SchoolSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.utils.ui.loaders.CohortLoader;
import com.projecthub.utils.ui.loaders.SchoolLoader;
import com.projecthub.utils.ui.loaders.TeamLoader;

@Component
public class LoaderFactory {

    @Autowired
    private SchoolLoader schoolLoader;

    @Autowired
    private CohortLoader cohortLoader;

    @Autowired
    private TeamLoader teamLoader;

    public TreeItemLoader getLoader(Object data) {
        if (data instanceof SchoolSummary) {
            return schoolLoader;
        } else if (data instanceof CohortSummary) {
            return cohortLoader;
        } else if (data instanceof TeamSummary) {
            return teamLoader;
        }
        return null;
    }
}