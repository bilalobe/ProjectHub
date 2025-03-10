package com.projecthub.base.shared.config;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

import java.sql.SQLException;

/**
 * Hibernate configuration class that sets up the JPA transaction manager
 * with a custom Hibernate JPA dialect to enable a filter for soft deletes.
 */
@Configuration
public class HibernateConfig {

    @Bean
    public JpaTransactionManager transactionManager(final LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        transactionManager.setJpaDialect(new HibernateJpaDialect() {

            @Override
            public @NonNull Object beginTransaction(@NonNull final EntityManager entityManager, @NonNull final TransactionDefinition definition) throws TransactionException, SQLException {
                final Object transaction = super.beginTransaction(entityManager, definition);
                final Session session = entityManager.unwrap(Session.class);
                session.enableFilter("deletedFilter").setParameter("isDeleted", false);
                return transaction;
            }
        });
        return transactionManager;
    }
}
