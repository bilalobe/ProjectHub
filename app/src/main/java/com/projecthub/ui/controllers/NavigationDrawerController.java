package com.projecthub.ui.controllers;

import com.projecthub.ui.services.NavigationService;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import javafx.fxml.FXML;

public class NavigationDrawerController {

    @FXML
    private NavigationDrawer navigationDrawer;

    @FXML
    private void navigateHome() {
        NavigationService.getInstance().navigateTo("/ui/views/HomeView.fxml");
        navigationDrawer.close();
    }

    @FXML
    private void navigateCohorts() {
        NavigationService.getInstance().navigateTo("/ui/views/CohortDetails.fxml");
        navigationDrawer.close();
    }

    @FXML
    private void navigateSettings() {
        NavigationService.getInstance().navigateTo("/ui/views/SettingsView.fxml");
        navigationDrawer.close();
    }

    // Add more navigation methods as needed
}
