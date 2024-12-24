package com.projecthub.core.config;

import com.projecthub.core.services.user.AppUserRoleService;
import com.projecthub.core.services.user.AppUserRoleServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleConfiguration {

    @Bean
    public AppUserRoleService roleService(AppUserRoleServiceImpl impl) {
        return impl;
    }
}