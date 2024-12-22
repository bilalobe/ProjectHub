package com.projecthub.core.utils;

import jakarta.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityReflectionUtils {
    private static final Logger log = LoggerFactory.getLogger(EntityReflectionUtils.class);

    private EntityReflectionUtils() {
        // Private constructor to prevent instantiation
    }

    public static List<Field> getEntityFields(Class<?> entityClass) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = entityClass;

        while (currentClass != null && currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }

        return fields.stream()
                .filter(field -> field.isAnnotationPresent(Column.class))
                .toList();
    }

    public static Object getFieldValue(Object entity, Field field) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            log.error("Error accessing field: {}", field.getName(), e);
            return null;
        }
    }
}
