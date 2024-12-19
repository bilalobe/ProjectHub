package com.projecthub.core.services.ui.locale;

import java.util.Locale;

public final class LocaleConstants {
    public static final String LOCALE_KEY = "app_locale";
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    protected static final Locale[] SUPPORTED_LOCALES = {
            Locale.ENGLISH,
            Locale.forLanguageTag("es"),
            Locale.forLanguageTag("fr"),
            Locale.forLanguageTag("ar"),

    };

    private LocaleConstants() {
    } // Prevent instantiation
}