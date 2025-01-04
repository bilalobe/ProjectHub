package com.projecthub.ui.shared.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TreeItemWrapper {

    private final String name;
    private final Object data;
    private final ObservableList<TreeItemWrapper> children = FXCollections.observableArrayList();

    public TreeItemWrapper(String name, Object data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public Object getData() {
        return data;
    }

    public ObservableList<TreeItemWrapper> getChildren() {
        return children;
    }
}