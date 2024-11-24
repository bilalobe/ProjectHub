package com.projecthub.utils.ui;

import javafx.scene.control.TreeItem;

public interface TreeItemLoader {
    void loadChildren(TreeItem<TreeItemWrapper> parentItem);
}