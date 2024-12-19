package com.projecthub.core.services.sync.impl;

import com.projecthub.core.services.sync.RemoteDataService;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PostgresRemoteDataService implements RemoteDataService {
    private final JdbcTemplate jdbcTemplate;

    public PostgresRemoteDataService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    @Retryable(retryFor = SQLException.class, maxAttempts = 3)
    public <T> List<T> getRemoteData(Class<T> entityClass) {
        String tableName = entityClass.getSimpleName().toLowerCase();
        String sql = String.format("SELECT * FROM %s", tableName);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(entityClass));
    }

    @Override
    @Transactional
    @Retryable(retryFor = SQLException.class, maxAttempts = 3)
    public <T> void saveRemoteData(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        Class<?> entityClass = entities.get(0).getClass();
        String tableName = entityClass.getSimpleName().toLowerCase();

        // Using batch update for better performance
        String sql = generateUpsertSql(tableName);
        jdbcTemplate.batchUpdate(sql, new EntityBatchPreparedStatementSetter<>(entities));
    }

    private String generateUpsertSql(String tableName) {
        // Implementation to generate UPSERT SQL based on entity class
        // This is a simplified version - you'd need to implement proper column mapping
        return String.format("INSERT INTO %s VALUES (?) ON CONFLICT (id) DO UPDATE SET value = EXCLUDED.value", tableName);
    }

    private static class EntityBatchPreparedStatementSetter<T> implements BatchPreparedStatementSetter {
        private final List<T> entities;

        public EntityBatchPreparedStatementSetter(List<T> entities) {
            this.entities = entities;
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
        }

        @Override
        public int getBatchSize() {
            return entities.size();
        }
    }
}