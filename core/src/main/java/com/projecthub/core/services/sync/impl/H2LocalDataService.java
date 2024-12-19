package com.projecthub.core.services.sync.impl;

import com.projecthub.core.services.sync.LocalDataService;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
        String sql = generateInsertSql(tableName);
        jdbcTemplate.batchUpdate(sql, new EntityBatchPreparedStatementSetter<>(entities));
    }

    @Override
    @Transactional
    public void clearLocalData(Class<?> entityClass) {
        String tableName = entityClass.getSimpleName().toLowerCase();
        String sql = String.format("DELETE FROM %s", tableName);
        jdbcTemplate.update(sql);
    }

    private String generateInsertSql(String tableName) {
        // Implementation to generate INSERT SQL based on entity class
        // This is a simplified version - you'd need to implement proper column mapping
        return String.format("INSERT INTO %s VALUES (?)", tableName);
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