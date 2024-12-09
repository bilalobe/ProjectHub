package com.projecthub.util.ui;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;

public class TreeCellFactory {

    public static TreeCell<TreeItemWrapper> createTreeCell(TreeView<TreeItemWrapper> treeView) {
        return new TreeCell<TreeItemWrapper>() {
            @Override
            protected void updateItem(TreeItemWrapper item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        };
    }
}