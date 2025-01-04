package com.projecthub.ui.core.services.locale;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
public class LanguageService {

    private final CopyOnWriteArrayList<Consumer<ResourceBundle>> listeners = new CopyOnWriteArrayList<>();
    private Locale currentLocale;
    private ResourceBundle resourceBundle;

    public LanguageService() {
        // Initialize with default locale (English)
        setLocale(Locale.ENGLISH);
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Sets a new locale and notifies all listeners to update their ResourceBundles.
     *
     * @param locale the new Locale to set
     */
    public void setLocale(Locale locale) {
        if (!locale.equals(this.currentLocale)) {
            this.currentLocale = locale;
            try {
                this.resourceBundle = ResourceBundle.getBundle("i18n/messages", currentLocale, new EnglishFallbackControl());
            } catch (Exception e) {
                // Fallback to English if specified locale is not found
                this.resourceBundle = ResourceBundle.getBundle("i18n/messages", Locale.ENGLISH);
            }
            notifyListeners();
        }
    }

    /**
     * Adds a listener that will be notified when the ResourceBundle changes.
     *
     * @param listener a Consumer that accepts the new ResourceBundle
     */
    public void addListener(Consumer<ResourceBundle> listener) {
        listeners.add(listener);
    }

    /**
     * Removes a previously added listener.
     *
     * @param listener the listener to remove
     */
    public void removeListener(Consumer<ResourceBundle> listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all listeners about the ResourceBundle change.
     */
    private void notifyListeners() {
        for (Consumer<ResourceBundle> listener : listeners) {
            listener.accept(resourceBundle);
        }
    }

    /**
     * Custom ResourceBundle.Control to handle fallback to English.
     */
    private static class EnglishFallbackControl extends ResourceBundle.Control {
        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            if (!locale.equals(Locale.ENGLISH)) {
                return Locale.ENGLISH;
            }
            return null;
        }
    }
}