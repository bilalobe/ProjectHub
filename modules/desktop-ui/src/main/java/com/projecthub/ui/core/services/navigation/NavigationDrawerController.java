package com.projecthub.ui.core.services.navigation;

import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.projecthub.ui.core.services.navigation.NavigationService;
import com.projecthub.ui.core.services.notification.NotificationService;
import javafx.fxml.FXML;

public class NavigationDrawerController {

    @FXML
    private NavigationDrawer navigationDrawer;

    @FXML
    private void navigateHome() {
        NotificationService.getInstance().navigateTo("/ui/views/HomeView.fxml");
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
