package com.projecthub.ui.modules.layout;

import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.projecthub.ui.core.services.NavigationService;
import com.projecthub.ui.core.services.SnackbarService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainLayoutController {

    @FXML
    private Button hamburgerButton;

    @FXML
    private NavigationDrawer navigationDrawer;

    @FXML
    private javafx.scene.layout.StackPane viewContainer;

    @FXML
    private com.gluonhq.charm.glisten.control.Snackbar globalSnackbar;

    @FXML
    private void initialize() {
        // Initialize NavigationService with the viewContainer
        NavigationService.getInstance().setViewContainer(viewContainer);

        // Initialize SnackbarService with the global Snackbar
        SnackbarService.getInstance().setSnackbar(globalSnackbar);

        // Set default view (e.g., Dashboard)
        NavigationService.getInstance().navigateTo("/ui/views/Dashboard.fxml");

        // Handle Hamburger Button Action to toggle Navigation Drawer
        hamburgerButton.setOnAction(event -> {
            navigationDrawer.toggle();
        });
    }
}
