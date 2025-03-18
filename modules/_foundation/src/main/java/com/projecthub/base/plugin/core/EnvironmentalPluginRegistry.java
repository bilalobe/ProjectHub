package com.projecthub.base.plugin.core;

import com.projecthub.base.plugin.api.EnvironmentalPlugin;
import com.projecthub.base.plugin.api.PluginConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.*;

@Component
public class EnvironmentalPluginRegistry {
    private static final Logger log = LoggerFactory.getLogger(EnvironmentalPluginRegistry.class);

    private final Map<String, EnvironmentalPlugin> activePlugins = new HashMap<>();
    private final ApplicationContext applicationContext;
    private final Path pluginsDirectory;

    public EnvironmentalPluginRegistry(
            ApplicationContext applicationContext,
            @Value("${projecthub.plugins.directory:plugins}") String pluginsDir) {
        this.applicationContext = applicationContext;
        this.pluginsDirectory = Path.of(pluginsDir);
    }

    @PostConstruct
    @Secured("ROLE_PLUGIN_ADMIN")
    public void initialize() {
        // Load internal plugins via Service Loader
        loadInternalPlugins();

        // Load external plugins from plugins directory
        loadExternalPlugins();

        // Initialize all discovered plugins
        initializePlugins();
    }

    @PreDestroy
    public void shutdown() {
        activePlugins.values().forEach(plugin -> {
            try {
                plugin.shutdown();
            } catch (RuntimeException e) {
                log.error("Error shutting down plugin: {}", plugin.getId(), e);
            }
        });
    }

    @Secured("ROLE_PLUGIN_ADMIN")
    public void registerPlugin(EnvironmentalPlugin plugin) {
        activePlugins.put(plugin.getId(), plugin);
    }

    @Secured("ROLE_ENVIRONMENTAL_READ")
    public List<EnvironmentalPlugin> getActivePlugins() {
        return new ArrayList<>(activePlugins.values());
    }

    @Secured("ROLE_PLUGIN_ADMIN")
    public void configurePlugin(@NonNls String pluginId, PluginConfig config) {
        EnvironmentalPlugin plugin = activePlugins.get(pluginId);
        if (plugin != null) {
            plugin.configure(config);
        } else {
            throw new IllegalArgumentException("Plugin not found: " + pluginId);
        }
    }

    private void loadInternalPlugins() {
        ServiceLoader<EnvironmentalPlugin> loader = ServiceLoader.load(EnvironmentalPlugin.class);
        loader.forEach(plugin -> activePlugins.put(plugin.getId(), plugin));
    }

    private static void loadExternalPlugins() {
        try {
            // Implementation will use DirectoryScanningRegistry approach
            // to load plugins from pluginsDirectory
        } catch (RuntimeException e) {
            log.error("Error loading external plugins", e);
        }
    }

    private void initializePlugins() {
        activePlugins.values().forEach(plugin -> {
            try {
                plugin.initialize();
            } catch (RuntimeException e) {
                log.error("Error initializing plugin: {}", plugin.getId(), e);
                activePlugins.remove(plugin.getId());
            }
        });
    }
}
