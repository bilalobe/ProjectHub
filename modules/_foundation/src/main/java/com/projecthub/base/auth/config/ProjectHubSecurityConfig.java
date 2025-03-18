package com.projecthub.base.auth.config;

import com.projecthub.base.auth.service.fortress.FortressAccessControlService;
import com.projecthub.base.auth.service.fortress.FortressAuthenticationProvider;
import com.projecthub.base.auth.service.fortress.FortressUserDetailsMapper;
import org.apache.directory.fortress.core.AccessMgr;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectHubSecurityConfig {
    private final AccessMgr accessManager;
    private final FortressUserDetailsMapper userDetailsMapper;

    public ProjectHubSecurityConfig(AccessMgr accessManager, FortressUserDetailsMapper userDetailsMapper) {
        this.accessManager = accessManager;
        this.userDetailsMapper = userDetailsMapper;
    }

    @Bean
    public FortressAuthenticationProvider authenticationProvider() {
        return new FortressAuthenticationProvider(accessManager, userDetailsMapper);
    }

    @Bean
    public FortressAccessControlService accessControlService() {
        return new FortressAccessControlService(accessManager);
    }
}
