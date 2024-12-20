package com.projecthub.ui.utils.loaders;

import com.projecthub.core.dto.TeamDTO;
import com.projecthub.ui.utils.PopulatorUtility;
import com.projecthub.ui.utils.TreeItemLoader;
import com.projecthub.ui.utils.TreeItemWrapper;
import javafx.scene.control.TreeItem;
import org.springframework.stereotype.Component;

@Component
public class TeamLoader implements TreeItemLoader {


    private PopulatorUtility populatorUtility;

    @Override
    public void load(TreeItem<TreeItemWrapper> parentItem, TreeItemWrapper parentWrapper) {
        TeamDTO team = (TeamDTO) parentWrapper.getData();
        populatorUtility.populateProjects(parentItem, team.getId());
    }
}