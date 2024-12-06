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

    @Override
    public void loadChildren(TreeItem<TreeItemWrapper> parentItem) {
        CohortDTO cohort = (CohortDTO) parentItem.getValue().getData();
        populatorUtility.populateTeams(parentItem, cohort.getId());
    }
}