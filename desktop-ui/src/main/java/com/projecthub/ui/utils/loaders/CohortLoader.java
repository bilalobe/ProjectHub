package com.projecthub.ui.utils.loaders;

import com.projecthub.core.dto.CohortDTO;
import com.projecthub.ui.utils.PopulatorUtility;
import com.projecthub.ui.utils.TreeItemLoader;
import com.projecthub.ui.utils.TreeItemWrapper;
import javafx.scene.control.TreeItem;
import org.springframework.stereotype.Component;

@Component
public class CohortLoader implements TreeItemLoader {


    private PopulatorUtility populatorUtility;

    public void load(TreeItem<TreeItemWrapper> parentItem, TreeItemWrapper parentWrapper) {
        CohortDTO cohort = (CohortDTO) parentWrapper.getData();
        populatorUtility.populateTeams(parentItem, cohort.getId());
    }
}