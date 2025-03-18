package com.projecthub.base.shared.utils;

import jakarta.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum EntityReflectionUtils {
    ;
    private static final Logger log = LoggerFactory.getLogger(EntityReflectionUtils.class);

    public static List<Field> getEntityFields(final Class<?> entityClass) {
        final Collection<Field> fields = new ArrayList<>();
        Class<?> currentClass = entityClass;

        while (null != currentClass && Object.class != currentClass) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }

        return fields.stream()
            .filter(field -> field.isAnnotationPresent(Column.class))
            .toList();
    }

    public static Object getFieldValue(final Object entity, final Field field) {
        try {
            return field.get(entity);
        } catch (final IllegalAccessException e) {
            EntityReflectionUtils.log.error("Error accessing field: {}", field.getName(), e);
            return null;
        }
    }
}
