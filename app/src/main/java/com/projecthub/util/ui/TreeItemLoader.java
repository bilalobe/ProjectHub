package com.projecthub.util.ui;

import javafx.scene.control.TreeItem;

public interface TreeItemLoader {
    void load(TreeItem<TreeItemWrapper> safeTreeItem, TreeItemWrapper parentWrapper);
}