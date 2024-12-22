package com.projecthub.ui.controllers;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.projecthub.ui.services.locale.LanguageService;
import com.projecthub.ui.viewmodels.MainViewViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class MainViewController extends BaseController {

    private final ApplicationContext applicationContext;
    private final LanguageService languageService;
    @FXML
    private BorderPane mainLayout;
    private NavigationDrawer navigationDrawer;
    private AppBar appBar;

    public MainViewController(MainViewViewModel mainViewViewModel,
                              ApplicationContext applicationContext,
                              LanguageService languageService) {
        this.applicationContext = applicationContext;
        this.languageService = languageService;
    }

    @FXML
    public void initialize() {
        setupAppBar();
        setupNavigationDrawer();
        setupContextMenu();
        showDashboard(); // Load the dashboard by default

        // Register listener for language changes
        languageService.addListener(this::updateLocalization);
    }

    /**
     * Sets up the AppBar with localized titles and actions.
     */
    private void setupAppBar() {
        appBar = new AppBar();

        // Navigation Icon (Hamburger Menu)
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> navigationDrawer.open()));

        // Action Icons
        appBar.getActionItems().add(MaterialDesignIcon.INFO.button(e -> handleAbout(e)));
        appBar.getActionItems().add(MaterialDesignIcon.SETTINGS.button(e -> handlePreferences(e)));

        // Set localized title
        updateAppBarTitle(languageService.getResourceBundle());

        mainLayout.setTop(appBar);
    }

    /**
     * Sets up the NavigationDrawer with localized menu items and language selection.
     */
    private void setupNavigationDrawer() {
        navigationDrawer = new NavigationDrawer();

        // Create Navigation Items with localized text
        NavigationDrawer.Item dashboardItem = createNavigationItem("menu.dashboard", MaterialDesignIcon.DASHBOARD, this::showDashboard);
        NavigationDrawer.Item projectsItem = createNavigationItem("menu.projects", MaterialDesignIcon.TASKS, this::showProjects);
        NavigationDrawer.Item teamsItem = createNavigationItem("menu.teams", MaterialDesignIcon.TEAMS, this::showTeams);
        NavigationDrawer.Item usersItem = createNavigationItem("menu.users", MaterialDesignIcon.ACCOUNT_BOX, this::showUsers);

        // Create Language Submenu
        NavigationDrawer.Item languageItem = new NavigationDrawer.Item("menu.language");
        languageItem.setGraphic(MaterialDesignIcon.LANGUAGE.graphic());

        // Submenu for English, Spanish, Arabic
        NavigationDrawer navigationDrawerLanguageSubmenu = new NavigationDrawer();
        NavigationDrawer.Item englishItem = createSubMenuItem("language.english", MaterialDesignIcon.LANGUAGE, () -> languageService.setLocale(Locale.ENGLISH));
        NavigationDrawer.Item spanishItem = createSubMenuItem("language.spanish", MaterialDesignIcon.LANGUAGE, () -> languageService.setLocale(Locale.of("es")));
        NavigationDrawer.Item arabicItem = createSubMenuItem("language.arabic", MaterialDesignIcon.LANGUAGE, () -> languageService.setLocale(Locale.of("ar")));

        navigationDrawerLanguageSubmenu.getItems().addAll(englishItem, spanishItem, arabicItem);
        languageItem.setContent(navigationDrawerLanguageSubmenu);

        navigationDrawer.getItems().addAll(dashboardItem, projectsItem, teamsItem, usersItem, languageItem);

        mainLayout.setLeft(navigationDrawer);

        // Apply initial localization
        updateNavigationDrawerItems(languageService.getResourceBundle());
    }

    /**
     * Creates a NavigationDrawer.Item with localized text and event handler.
     *
     * @param key              the ResourceBundle key for the menu text
     * @param icon             the MaterialDesignIcon to display
     * @param navigationAction the action to perform on click
     * @return the configured NavigationDrawer.Item
     */
    private NavigationDrawer.Item createNavigationItem(String key, MaterialDesignIcon icon, Runnable navigationAction) {
        NavigationDrawer.Item item = new NavigationDrawer.Item(key);
        item.setGraphic(icon.graphic());
        item.setOnMouseClicked(e -> handleNavigation(navigationAction));
        return item;
    }

    /**
     * Creates a sub-menu item for language selection.
     *
     * @param key    the ResourceBundle key for the language
     * @param icon   the MaterialDesignIcon to display
     * @param action the action to perform on click
     * @return the configured NavigationDrawer.Item
     */
    private NavigationDrawer.Item createSubMenuItem(String key, MaterialDesignIcon icon, Runnable action) {
        NavigationDrawer.Item item = new NavigationDrawer.Item(key);
        item.setGraphic(icon.graphic());
        item.setOnMouseClicked(e -> {
            action.run();
            navigationDrawer.close();
        });
        return item;
    }

    /**
     * Sets up the context menu with localized items.
     */
    private void setupContextMenu() {
        // Context Menu can be implemented similarly with localization
        // For brevity, it's kept as a simple context menu with fixed text
    }

    /**
     * Handles navigation by closing the drawer and executing the provided action.
     *
     * @param action the action to execute
     */
    private void handleNavigation(Runnable action) {
        navigationDrawer.close();
        action.run();
    }

    /**
     * Loads the specified FXML view into the content area.
     *
     * @param fxmlPath the path to the FXML file
     */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            // Set the ResourceBundle for localization
            loader.setResources(languageService.getResourceBundle());
            // Set controller factory for Spring dependency injection
            loader.setControllerFactory(applicationContext::getBean);
            Node view = loader.load();
            mainLayout.setCenter(view);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, localize("alert.title.error"), localize("error.failed.to.load.view"));
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handlePreferences(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, localize("alert.title.information"), localize("preferences.open"));
    }

    @FXML
    private void handleAbout(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, localize("alert.title.about"), localize("about.content"));
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, localize("alert.title.information"), localize("refresh.content"));
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, localize("alert.title.information"), localize("settings.open"));
    }

    @FXML
    private void showDashboard() {
        loadView("/com/projecthub/ui/views/DashboardView.fxml");
    }

    @FXML
    private void showProjects() {
        loadView("/com/projecthub/ui/views/ProjectsView.fxml");
    }

    @FXML
    private void showTeams() {
        loadView("/com/projecthub/ui/views/TeamsView.fxml");
    }

    @FXML
    private void showUsers() {
        loadView("/com/projecthub/ui/views/UsersView.fxml");
    }

    /**
     * Updates UI components with localized texts when the locale changes.
     *
     * @param resourceBundle the new ResourceBundle
     */
    private void updateLocalization(ResourceBundle resourceBundle) {
        // Update AppBar title
        updateAppBarTitle(resourceBundle);

        // Update NavigationDrawer items
        updateNavigationDrawerItems(resourceBundle);

        // Reload the current view to apply localization
        Node currentView = mainLayout.getCenter();
        if (currentView != null) {
            // Optionally, refresh or reload the view if dynamic updates are needed
            // For simplicity, resizing to force a refresh
            currentView.getScene().getWindow().sizeToScene();
        }
    }

    /**
     * Updates the AppBar title based on the ResourceBundle.
     *
     * @param resourceBundle the current ResourceBundle
     */
    private void updateAppBarTitle(ResourceBundle resourceBundle) {
        if (appBar != null) {
            String title = resourceBundle.getString("main.title");
            appBar.setTitleText(title);
        }
    }

    /**
     * Updates the NavigationDrawer items with localized text.
     *
     * @param resourceBundle the current ResourceBundle
     */
    private void updateNavigationDrawerItems(ResourceBundle resourceBundle) {
        for (NavigationDrawer.Item item : navigationDrawer.getItems()) {
            String key = item.getText();
            if (key.startsWith("menu.")) { // Ensure it's a menu item
                item.setText(resourceBundle.getString(key));
            }
        }
    }
}