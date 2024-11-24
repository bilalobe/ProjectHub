package com.projecthub.ui.controllers.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.ui.viewmodels.ProjectHubViewModel;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@Component
public class MainController {

    @Autowired
    private ProjectHubViewModel viewModel;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        bindProperties();
    }

    private void bindProperties() {
        // Bind searchField to ViewModel's searchQueryProperty
        searchField.textProperty().bindBidirectional(viewModel.searchQueryProperty());
    }
}