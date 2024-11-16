package com.projecthub.ui.controllers;

import com.projecthub.model.Class;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class ClassDetailsController {

    @FXML
    private Label classNameLabel;

    private Class Class;

    public void setClass(Class Class) {
        this.Class = Class;
        updateUI();
    }

    private void updateUI() {
        classNameLabel.setText(Class.getName());
        // Update other UI elements
    }
}