package com.projecthub.ui.shared.utils;

import javafx.scene.control.TreeItem;

public interface TreeItemLoader {
    void load(TreeItem<TreeItemWrapper> safeTreeItem, TreeItemWrapper parentWrapper);
}