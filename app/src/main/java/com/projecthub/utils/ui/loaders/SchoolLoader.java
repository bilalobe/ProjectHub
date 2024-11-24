package com.projecthub.utils.ui.loaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.SchoolSummary;
import com.projecthub.utils.ui.PopulatorUtility;
import com.projecthub.utils.ui.TreeItemLoader;
import com.projecthub.utils.ui.TreeItemWrapper;

import javafx.scene.control.TreeItem;

@Component
public class SchoolLoader implements TreeItemLoader {

    @Autowired
    private PopulatorUtility populatorUtility;

    @Override
    public void loadChildren(TreeItem<TreeItemWrapper> parentItem) {
        SchoolSummary school = (SchoolSummary) parentItem.getValue().getData();
        populatorUtility.populateCohorts(parentItem, school.getId());
    }
}