package com.projecthub.ui.controllers;

import com.projecthub.model.Cohort;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class CohortDetailsController {

    @FXML
    private Label classNameLabel;

    private Cohort Class;

    public void setCohort(Cohort Class) {
        this.Class = Class;
        updateUI();
    }

    private void updateUI() {
        classNameLabel.setText(Class.getName());
        // Update other UI elements
    }
}