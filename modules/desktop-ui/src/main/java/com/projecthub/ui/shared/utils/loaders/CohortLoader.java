package com.projecthub.ui.shared.utils.loaders;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.ui.shared.utils.PopulatorUtility;
import com.projecthub.ui.shared.utils.TreeItemLoader;
import com.projecthub.ui.shared.utils.TreeItemWrapper;
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
