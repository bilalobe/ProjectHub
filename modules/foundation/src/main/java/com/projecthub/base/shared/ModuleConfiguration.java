package com.projecthub.base.shared;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

@Slf4j
@Configuration
public class ModuleConfiguration {

    private ModuleConfiguration() {
        // Private constructor to hide the implicit public one
    }

    @Component
    public static class CoreModuleEventListener {

        @EventListener
        @ApplicationModuleListener(
            id = "base-module",
            readOnlyTransaction = true,
            propagation = Propagation.REQUIRED
        )
        public void onApplicationEvent(final ApplicationEvent event) {
            ModuleConfiguration.log.info("Processing event: {}", event.getClass().getSimpleName());
        }
    }
}
