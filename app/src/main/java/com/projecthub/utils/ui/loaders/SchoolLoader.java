package com.projecthub.utils.ui.loaders;

import org.springframework.stereotype.Component;

import com.projecthub.dto.SchoolDTO;
import com.projecthub.utils.ui.PopulatorUtility;
import com.projecthub.utils.ui.TreeItemLoader;
import com.projecthub.utils.ui.TreeItemWrapper;

import javafx.scene.control.TreeItem;

@Component
public class SchoolLoader implements TreeItemLoader {

    
    private PopulatorUtility populatorUtility;

    @Override
    public void loadChildren(TreeItem<TreeItemWrapper> parentItem) {
        SchoolDTO school = (SchoolDTO) parentItem.getValue().getData();
        populatorUtility.populateCohorts(parentItem, school.getId());
    }
}