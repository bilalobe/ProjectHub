package com.projecthub.base;

import com.projecthub.base.shared.EnableModulith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootApplication
@EnableModulith
@EnableJpaRepositories(basePackages = "com.projecthub.base")
public class CoreApplication {
    public CoreApplication() {
    }

    public static void main(final String[] args) {
        final ApplicationModules modules = ApplicationModules.of(CoreApplication.class);
        modules.verify();
        SpringApplication.run(CoreApplication.class, args);
    }
}
