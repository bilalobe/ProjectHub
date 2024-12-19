package com.projecthub.core.services.ui.theme;

import javafx.scene.Scene;
import org.springframework.stereotype.Service;

import java.util.prefs.Preferences;

/**
 * Service to manage application themes (Light and Dark).
 */
@Service
public class ThemeService {

    private static final String THEME_KEY = "app_theme";
    private static final String LIGHT_THEME = "/css/gluon-light.css";
    private static final String DARK_THEME = "/css/gluon-dark.css";
    private final Preferences prefs = Preferences.userNodeForPackage(ThemeService.class);
    private String currentTheme;
    private Scene scene;

    /**
     * Initializes ThemeService without Scene. Scene will be set later.
     */
    public ThemeService() {
        this.currentTheme = prefs.get(THEME_KEY, "Light"); // Default to Light theme
    }

    /**
     * Sets the Scene for ThemeService.
     *
     * @param scene the primary Scene of the application
     */
    public void setScene(Scene scene) {
        this.scene = scene;
        applyTheme(currentTheme);
    }

    /**
     * Sets the application theme and persists the preference.
     *
     * @param theme the name of the theme to set ("Light" or "Dark")
     */
    public void setTheme(String theme) {
        this.currentTheme = theme;
        applyTheme(theme);
        prefs.put(THEME_KEY, theme); // Persist the theme preference
    }

    /**
     * Retrieves the current theme.
     *
     * @return the current theme name
     */
    public String getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Applies the specified theme to the application's scene.
     *
     * @param theme the name of the theme to apply
     */
    private void applyTheme(String theme) {
        if (scene == null) {
            // Scene not set yet
            return;
        }

        scene.getStylesheets().removeIf(css -> css.contains("gluon-light.css") || css.contains("gluon-dark.css"));

        switch (theme) {
            case "Dark":
                scene.getStylesheets().add(getClass().getResource(DARK_THEME).toExternalForm());
                break;
            case "Light":
            default:
                scene.getStylesheets().add(getClass().getResource(LIGHT_THEME).toExternalForm());
                break;
        }
    }
}