package com.projecthub.ui.shared.utils.loaders;

import com.projecthub.base.school.api.dto.SchoolDTO;
import com.projecthub.ui.shared.utils.PopulatorUtility;
import com.projecthub.ui.shared.utils.TreeItemLoader;
import com.projecthub.ui.shared.utils.TreeItemWrapper;
import javafx.scene.control.TreeItem;
import org.springframework.stereotype.Component;

@Component
public class SchoolLoader implements TreeItemLoader {


    private PopulatorUtility populatorUtility;

    @Override
    public void load(TreeItem<TreeItemWrapper> parentItem, TreeItemWrapper parentWrapper) {
        SchoolDTO school = (SchoolDTO) parentWrapper.getData();
        populatorUtility.populateCohorts(parentItem, school.getId());
    }
}
