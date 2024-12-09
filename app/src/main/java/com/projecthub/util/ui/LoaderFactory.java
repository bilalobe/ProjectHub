package com.projecthub.util.ui;

import org.springframework.stereotype.Component;

import com.projecthub.dto.CohortDTO;
import com.projecthub.dto.SchoolDTO;
import com.projecthub.dto.TeamDTO;
import com.projecthub.util.ui.loaders.CohortLoader;
import com.projecthub.util.ui.loaders.SchoolLoader;
import com.projecthub.util.ui.loaders.TeamLoader;

@Component
public class LoaderFactory {

    
    private SchoolLoader schoolLoader;

    
    private CohortLoader cohortLoader;

    
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