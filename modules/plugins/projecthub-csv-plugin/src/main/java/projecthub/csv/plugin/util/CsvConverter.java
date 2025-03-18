package projecthub.csv.plugin.util;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CsvConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public CsvConverter() {
    }

    public static class UUIDConverter extends AbstractBeanField<UUID, String> {
        public UUIDConverter() {
        }

        @Override
        protected UUID convert(@org.jetbrains.annotations.NonNls String value) throws CsvDataTypeMismatchException {
            try {
                return value == null || value.trim().isEmpty() ? null : UUID.fromString(value);
            } catch (IllegalArgumentException e) {
                throw new CsvDataTypeMismatchException(value, UUID.class, "Invalid UUID format");
            }
        }
    }

    public static class LocalDateTimeConverter extends AbstractBeanField<LocalDateTime, String> {
        public LocalDateTimeConverter() {
        }

        @Override
        protected LocalDateTime convert(@org.jetbrains.annotations.NonNls String value) throws CsvDataTypeMismatchException {
            try {
                return value == null || value.trim().isEmpty() ? null : LocalDateTime.parse(value, DATE_TIME_FORMATTER);
            } catch (RuntimeException e) {
                throw new CsvDataTypeMismatchException(value, LocalDateTime.class, "Invalid DateTime format");
            }
        }
    }

    public static String formatDateTime(ChronoLocalDateTime<LocalDate> dateTime) {
        return dateTime == null ? "" : dateTime.format(DATE_TIME_FORMATTER);
    }
}
