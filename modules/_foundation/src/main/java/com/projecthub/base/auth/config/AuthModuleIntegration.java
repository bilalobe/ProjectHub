package com.projecthub.base.auth.config;

import com.projecthub.auth.config.SecurityAutoConfiguration;
import com.projecthub.auth.facade.AuthenticationFacade;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

@Configuration
@Order(1)
@Import(SecurityAutoConfiguration.class)
public class AuthModuleIntegration {
    private final AuthenticationFacade authenticationFacade;

    public AuthModuleIntegration(AuthenticationFacade authenticationFacade) {
        this.authenticationFacade = authenticationFacade;
    }

    // Expose any necessary foundation-specific authentication beans or overrides here
}
