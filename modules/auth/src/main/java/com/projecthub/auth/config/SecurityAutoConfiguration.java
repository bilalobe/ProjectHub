package com.projecthub.auth.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {
    "com.projecthub.auth.aspect",
    "com.projecthub.auth.service"
})
public class SecurityAutoConfiguration {
}