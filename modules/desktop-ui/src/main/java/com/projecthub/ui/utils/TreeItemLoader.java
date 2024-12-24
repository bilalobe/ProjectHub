package com.projecthub.ui.utils;

import javafx.scene.control.TreeItem;

public interface TreeItemLoader {
    void load(TreeItem<TreeItemWrapper> safeTreeItem, TreeItemWrapper parentWrapper);
}