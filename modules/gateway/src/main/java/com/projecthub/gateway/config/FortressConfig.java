package com.projecthub.gateway.config;

import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.AccessMgrFactory;
import org.apache.directory.fortress.core.SecurityException;
import org.apache.directory.fortress.core.impl.TestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Apache Fortress integration.
 * 
 * <p>This class sets up the Fortress AccessMgr bean which is used by the
 * gateway for making authorization decisions.</p>
 */
@Configuration
public class FortressConfig {

    @Value("${projecthub.auth.fortress.bootstrap:false}")
    private boolean bootstrapFortress;
    
    @Value("${projecthub.auth.fortress.config-file:fortress.properties}")
    private String fortressConfigFile;

    /**
     * Creates the Fortress AccessMgr bean.
     * 
     * @return The configured AccessMgr instance
     * @throws SecurityException If Fortress configuration fails
     */
    @Bean
    public AccessMgr accessManager() throws SecurityException {
        // Set the system property for Fortress config file location
        System.setProperty("fortress.config.file", fortressConfigFile);
        
        // Create the access manager
        AccessMgr accessMgr = AccessMgrFactory.createInstance();
        
        // Bootstrap Fortress with test data if enabled
        if (bootstrapFortress) {
            TestUtils.initializeDirectory(null);
        }
        
        return accessMgr;
    }
}