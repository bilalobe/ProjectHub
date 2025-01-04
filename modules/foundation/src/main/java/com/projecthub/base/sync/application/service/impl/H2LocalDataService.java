package com.projecthub.base.sync.application.service.impl;

import com.projecthub.base.shared.utils.EntityReflectionUtils;
import com.projecthub.base.sync.application.service.LocalDataService;
import jakarta.persistence.Column;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class H2LocalDataService implements LocalDataService {
    private final JdbcTemplate jdbcTemplate;

    public H2LocalDataService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> getLocalData(Class<T> entityClass) {
        String tableName = entityClass.getSimpleName().toLowerCase();
        String sql = String.format("SELECT * FROM %s", tableName);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(entityClass));
    }

    @Override
    @Transactional
    public <T> void saveLocalData(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        Class<?> entityClass = entities.get(0).getClass();
        String tableName = entityClass.getSimpleName().toLowerCase();

        // Using batch update for better performance
        String sql = generateInsertSql(tableName, entityClass);
        jdbcTemplate.batchUpdate(sql, new EntityBatchPreparedStatementSetter<>(entities));
    }

    @Override
    @Transactional
    public void clearLocalData(Class<?> entityClass) {
        String tableName = entityClass.getSimpleName().toLowerCase();
        String sql = String.format("DELETE FROM %s", tableName);
        jdbcTemplate.update(sql);
    }

    private String generateInsertSql(String tableName, Class<?> entityClass) {
        List<Field> fields = EntityReflectionUtils.getEntityFields(entityClass);
        String columns = fields.stream()
            .map(field -> field.getAnnotation(Column.class).name())
            .collect(Collectors.joining(", "));

        String values = fields.stream()
            .map(_ -> "?")
            .collect(Collectors.joining(", "));

        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);
    }

    private static class EntityBatchPreparedStatementSetter<T> implements BatchPreparedStatementSetter {
        private final List<T> entities;
        private final List<Field> fields;

        public EntityBatchPreparedStatementSetter(List<T> entities) {
            this.entities = entities;
            this.fields = EntityReflectionUtils.getEntityFields(entities.get(0).getClass());
        }

        @Override
        public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
            T entity = entities.get(i);
            int paramIndex = 1;

            for (Field field : fields) {
                Object value = EntityReflectionUtils.getFieldValue(entity, field);
                if (value != null) {
                    ps.setObject(paramIndex++, value);
                } else {
                    ps.setNull(paramIndex++, getSqlType(field.getType()));
                }
            }
        }

        @Override
        public int getBatchSize() {
            return entities.size();
        }

        private int getSqlType(Class<?> type) {
            if (type == String.class) return Types.VARCHAR;
            if (type == Integer.class) return Types.INTEGER;
            if (type == Long.class) return Types.BIGINT;
            if (type == Boolean.class) return Types.BOOLEAN;
            if (type == LocalDateTime.class) return Types.TIMESTAMP;
            return Types.OTHER;
        }
    }
}
