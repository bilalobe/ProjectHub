package com.projecthub.utils.ui.loaders;

import org.springframework.stereotype.Component;

import com.projecthub.dto.CohortDTO;
import com.projecthub.utils.ui.PopulatorUtility;
import com.projecthub.utils.ui.TreeItemLoader;
import com.projecthub.utils.ui.TreeItemWrapper;

import javafx.scene.control.TreeItem;

@Component
public class CohortLoader implements TreeItemLoader {

    
    private PopulatorUtility populatorUtility;

    public void load(TreeItem<TreeItemWrapper> parentItem, TreeItemWrapper parentWrapper) {
        CohortDTO cohort = (CohortDTO) parentWrapper.getData();
        populatorUtility.populateTeams(parentItem, cohort.getId());
    }
}