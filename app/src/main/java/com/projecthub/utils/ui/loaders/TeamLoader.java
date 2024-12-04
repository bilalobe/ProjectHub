package com.projecthub.utils.ui.loaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.TeamDTO;
import com.projecthub.utils.ui.PopulatorUtility;
import com.projecthub.utils.ui.TreeItemLoader;
import com.projecthub.utils.ui.TreeItemWrapper;

import javafx.scene.control.TreeItem;

@Component
public class TeamLoader implements TreeItemLoader {

    @Autowired
    private PopulatorUtility populatorUtility;

    @Override
    public void loadChildren(TreeItem<TreeItemWrapper> parentItem) {
        TeamDTO team = (TeamDTO) parentItem.getValue().getData();
        populatorUtility.populateProjects(parentItem, team.getId());
    }
}