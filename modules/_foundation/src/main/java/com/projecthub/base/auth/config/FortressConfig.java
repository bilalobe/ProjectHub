package com.projecthub.base.auth.config;

import org.apache.directory.fortress.core.*;
import org.apache.directory.fortress.core.impl.AccessMgrImpl;
import org.apache.directory.fortress.core.impl.AdminMgrImpl;
import org.apache.directory.fortress.core.impl.DelAdminMgrImpl;
import org.apache.directory.fortress.core.impl.ReviewMgrImpl;
import org.apache.directory.fortress.realm.impl.FortressJdbcRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Configuration class for Apache Fortress integration.
 * This class defines the necessary beans for Fortress's RBAC implementation.
 */
@Configuration
public class FortressConfig {

    private static final Logger log = LoggerFactory.getLogger(FortressConfig.class);

    public FortressConfig() {
    }

    /**
     * Initialize the Fortress security context.
     *
     * @return true if the security context was successfully initialized
     */
    @Bean
    public boolean fortressInit() {
        boolean success = false;
        try {
            log.info("Initializing Apache Fortress security context");
            success = SecurityContextFactory.initializeSecurityContext();
            log.info("Apache Fortress security context initialized successfully: {}", Boolean.valueOf(success));
            return success;
        } catch (RuntimeException e) {
            log.error("Failed to initialize Apache Fortress security context", e);
            return false;
        }
    }

    /**
     * Creates the Fortress Access Manager bean.
     * The AccessMgr is responsible for checking user authentication and authorization.
     *
     * @return AccessMgr instance
     */
    @Bean
    @DependsOn("fortressInit")
    public AccessMgr accessManager() {
        try {
            log.info("Creating Fortress AccessMgr bean");
            return new AccessMgrImpl();
        } catch (RuntimeException e) {
            log.error("Failed to create Fortress AccessMgr bean", e);
            return null;
        }
    }

    /**
     * Creates the Fortress Admin Manager bean.
     * The AdminMgr is responsible for administering users, roles, and permissions.
     *
     * @return AdminMgr instance
     */
    @Bean
    @DependsOn("fortressInit")
    public AdminMgr adminManager() {
        try {
            log.info("Creating Fortress AdminMgr bean");
            return new AdminMgrImpl();
        } catch (RuntimeException e) {
            log.error("Failed to create Fortress AdminMgr bean", e);
            return null;
        }
    }

    /**
     * Creates the Fortress Delegated Admin Manager bean.
     * The DelAdminMgr is responsible for delegated administration of users, roles, and permissions.
     *
     * @return DelAdminMgr instance
     */
    @Bean
    @DependsOn("fortressInit")
    public DelAdminMgr delAdminManager() {
        try {
            log.info("Creating Fortress DelAdminMgr bean");
            return new DelAdminMgrImpl();
        } catch (RuntimeException e) {
            log.error("Failed to create Fortress DelAdminMgr bean", e);
            return null;
        }
    }

    /**
     * Creates the Fortress Review Manager bean.
     * The ReviewMgr is responsible for reviewing users, roles, and permissions.
     *
     * @return ReviewMgr instance
     */
    @Bean
    @DependsOn("fortressInit")
    public ReviewMgr reviewManager() {
        try {
            log.info("Creating Fortress ReviewMgr bean");
            return new ReviewMgrImpl();
        } catch (RuntimeException e) {
            log.error("Failed to create Fortress ReviewMgr bean", e);
            return null;
        }
    }

    /**
     * Creates the Fortress JDBC Realm bean.
     * This realm integrates with Spring Security for authentication and authorization.
     *
     * @return FortressJdbcRealm instance
     */
    @Bean
    @DependsOn("fortressInit")
    public FortressJdbcRealm fortressRealm() {
        log.info("Creating Fortress JDBC Realm bean");
        return new FortressJdbcRealm();
    }
}
