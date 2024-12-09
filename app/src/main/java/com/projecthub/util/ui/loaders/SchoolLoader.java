package com.projecthub.util.ui.loaders;

import org.springframework.stereotype.Component;

import com.projecthub.dto.SchoolDTO;
import com.projecthub.util.ui.PopulatorUtility;
import com.projecthub.util.ui.TreeItemLoader;
import com.projecthub.util.ui.TreeItemWrapper;

import javafx.scene.control.TreeItem;

@Component
public class SchoolLoader implements TreeItemLoader {

    
    private PopulatorUtility populatorUtility;

    @Override
    public void load(TreeItem<TreeItemWrapper> parentItem, TreeItemWrapper parentWrapper) {
        SchoolDTO school = (SchoolDTO) parentWrapper.getData();
        populatorUtility.populateCohorts(parentItem, school.getId());
    }
}