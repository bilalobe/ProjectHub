package com.projecthub.utils.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.CohortDTO;
import com.projecthub.dto.SchoolDTO;
import com.projecthub.dto.TeamDTO;
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