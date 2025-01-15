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

    public H2LocalDataService(final DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> getLocalData(final Class<T> entityClass) {
        final String tableName = entityClass.getSimpleName().toLowerCase();
        final String sql = String.format("SELECT * FROM %s", tableName);
        return this.jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(entityClass));
    }

    @Override
    @Transactional
    public <T> void saveLocalData(final List<T> entities) {
        if (null == entities || entities.isEmpty()) {
            return;
        }

        final Class<?> entityClass = entities.get(0).getClass();
        final String tableName = entityClass.getSimpleName().toLowerCase();

        // Using batch update for better performance
        final String sql = this.generateInsertSql(tableName, entityClass);
        this.jdbcTemplate.batchUpdate(sql, new EntityBatchPreparedStatementSetter<>(entities));
    }

    @Override
    @Transactional
    public void clearLocalData(final Class<?> entityClass) {
        final String tableName = entityClass.getSimpleName().toLowerCase();
        final String sql = String.format("DELETE FROM %s", tableName);
        this.jdbcTemplate.update(sql);
    }

    private String generateInsertSql(final String tableName, final Class<?> entityClass) {
        final List<Field> fields = EntityReflectionUtils.getEntityFields(entityClass);
        final String columns = fields.stream()
            .map(field -> field.getAnnotation(Column.class).name())
            .collect(Collectors.joining(", "));

        final String values = fields.stream()
            .map(_ -> "?")
            .collect(Collectors.joining(", "));

        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);
    }

    private static class EntityBatchPreparedStatementSetter<T> implements BatchPreparedStatementSetter {
        private final List<T> entities;
        private final List<Field> fields;

        public EntityBatchPreparedStatementSetter(final List<T> entities) {
            this.entities = entities;
            fields = EntityReflectionUtils.getEntityFields(entities.get(0).getClass());
        }

        @Override
        public void setValues(@NonNull final PreparedStatement ps, final int i) throws SQLException {
            final T entity = this.entities.get(i);
            int paramIndex = 1;

            for (final Field field : this.fields) {
                final Object value = EntityReflectionUtils.getFieldValue(entity, field);
                if (null != value) {
                    ps.setObject(paramIndex, value);
                    paramIndex++;
                } else {
                    ps.setNull(paramIndex, this.getSqlType(field.getType()));
                    paramIndex++;
                }
            }
        }

        @Override
        public int getBatchSize() {
            return this.entities.size();
        }

        private int getSqlType(final Class<?> type) {
            if (String.class == type) return Types.VARCHAR;
            if (Integer.class == type) return Types.INTEGER;
            if (Long.class == type) return Types.BIGINT;
            if (Boolean.class == type) return Types.BOOLEAN;
            if (LocalDateTime.class == type) return Types.TIMESTAMP;
            return Types.OTHER;
        }
    }
}
