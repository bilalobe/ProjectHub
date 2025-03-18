package com.projecthub.plugin

import com.projecthub.security.FortressSecurityAdapter
import com.projecthub.session.UserSession
import com.typesafe.config.ConfigFactory
import java.io.File
import java.net.URLClassLoader
import java.util.ServiceLoader
import java.util.concurrent.ConcurrentHashMap
import org.slf4j.LoggerFactory
import java.util.Properties

/**
 * Implementation of a plugin registry that discovers plugins through multiple methods:
 * - SPI (ServiceLoader)
 * - Directory scanning
 * - Configuration-based discovery
 */
class HybridPluginRegistry(
    private val securityHub: FortressSecurityHub
) : PluginRegistry {
    private val plugins = ConcurrentHashMap<String, ProjectHubPlugin>()
    private val lifecycleStates = ConcurrentHashMap<String, PluginLifecycleState>()
    private val logger = LoggerFactory.getLogger(HybridPluginRegistry::class.java)
    
    enum class PluginLifecycleState {
        DISCOVERED, INITIALIZED, ACTIVE, STOPPED, ERROR
    }
    
    override fun getPlugins(): List<ProjectHubPlugin> = plugins.values.toList()
    
    override fun getPlugin(id: String): ProjectHubPlugin? = plugins[id]
    
    override fun registerPlugin(plugin: ProjectHubPlugin) {
        plugins[plugin.id] = plugin
        lifecycleStates[plugin.id] = PluginLifecycleState.DISCOVERED
    }
    
    fun discoverPlugins() {
        discoverSpiPlugins()
        discoverDirectoryPlugins()
        discoverConfigPlugins()
        
        // Initialize discovered plugins
        plugins.values.forEach { initializePlugin(it) }
    }
    
    private fun discoverSpiPlugins() {
        ServiceLoader.load(ProjectHubPlugin::class.java).forEach { plugin -> 
            plugins[plugin.id] = plugin
            lifecycleStates[plugin.id] = PluginLifecycleState.DISCOVERED
        }
    }
    
    private fun discoverDirectoryPlugins() {
        val pluginsDir = File("plugins")
        if (!pluginsDir.exists()) {
            pluginsDir.mkdirs()
            logger.info("Created plugins directory at ${pluginsDir.absolutePath}")
            return
        }
        
        pluginsDir.listFiles()?.forEach { file ->
            if (file.extension == "jar") {
                try {
                    logger.info("Loading plugin from ${file.name}")
                    val pluginDescriptor = loadPluginDescriptor(file)
                    val classLoader = URLClassLoader(arrayOf(file.toURI().toURL()), this.javaClass.classLoader)
                    
                    val pluginClass = if (pluginDescriptor != null && pluginDescriptor.mainClass.isNotBlank()) {
                        classLoader.loadClass(pluginDescriptor.mainClass)
                    } else {
                        classLoader.loadClass("com.projecthub.plugin.Plugin")
                    }
                    
                    val plugin = pluginClass.getDeclaredConstructor().newInstance() as ProjectHubPlugin
                    plugins[plugin.id] = plugin
                    lifecycleStates[plugin.id] = PluginLifecycleState.DISCOVERED
                    logger.info("Successfully loaded plugin ${plugin.id} (${plugin.name})")
                } catch (e: Exception) {
                    logger.error("Failed to load plugin from ${file.name}", e)
                }
            }
        }
    }
    
    private fun loadPluginDescriptor(jarFile: File): PluginDescriptor? {
        try {
            val jar = java.util.jar.JarFile(jarFile)
            val entry = jar.getEntry("plugin.properties")
            
            if (entry != null) {
                val props = Properties()
                jar.getInputStream(entry).use { 
                    props.load(it)
                }
                
                return PluginDescriptor(
                    mainClass = props.getProperty("plugin.class", ""),
                    version = props.getProperty("plugin.version", "1.0.0"),
                    author = props.getProperty("plugin.author", ""),
                    website = props.getProperty("plugin.website", "")
                )
            }
        } catch (e: Exception) {
            logger.warn("Failed to load plugin descriptor from $jarFile", e)
        }
        
        return null
    }
    
    private fun discoverConfigPlugins() {
        try {
            val config = ConfigFactory.load()
            if (config.hasPath("projecthub.plugins")) {
                val configPlugins = config.getConfig("projecthub.plugins")
                configPlugins.entrySet().forEach { entry ->
                    try {
                        val className = entry.value.unwrapped().toString()
                        val plugin = Class.forName(className).getDeclaredConstructor().newInstance() as ProjectHubPlugin
                        plugins[plugin.id] = plugin
                        lifecycleStates[plugin.id] = PluginLifecycleState.DISCOVERED
                    } catch (e: Exception) {
                        println("Failed to load plugin ${entry.key}: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            println("Error loading plugin configuration: ${e.message}")
        }
    }
    
    fun initializePlugin(plugin: ProjectHubPlugin) {
        try {
            plugin.initialize()
            lifecycleStates[plugin.id] = PluginLifecycleState.INITIALIZED
        } catch (e: Exception) {
            lifecycleStates[plugin.id] = PluginLifecycleState.ERROR
            println("Failed to initialize plugin ${plugin.id}: ${e.message}")
        }
    }
    
    fun startPlugin(plugin: ProjectHubPlugin) {
        if (lifecycleStates[plugin.id] == PluginLifecycleState.INITIALIZED) {
            try {
                plugin.start()
                lifecycleStates[plugin.id] = PluginLifecycleState.ACTIVE
            } catch (e: Exception) {
                lifecycleStates[plugin.id] = PluginLifecycleState.ERROR
                println("Failed to start plugin ${plugin.id}: ${e.message}")
            }
        }
    }
    
    fun stopPlugin(plugin: ProjectHubPlugin) {
        if (lifecycleStates[plugin.id] == PluginLifecycleState.ACTIVE) {
            try {
                plugin.stop()
                lifecycleStates[plugin.id] = PluginLifecycleState.STOPPED
            } catch (e: Exception) {
                lifecycleStates[plugin.id] = PluginLifecycleState.ERROR
                println("Failed to stop plugin ${plugin.id}: ${e.message}")
            }
        }
    }
    
    fun getPluginState(pluginId: String): PluginLifecycleState? = lifecycleStates[pluginId]
    
    fun validatePluginSecurity(plugin: ProjectHubPlugin, session: UserSession): Boolean {
        return securityAdapter.checkPermission(session, "plugin:${plugin.id}:execute")
    }
    
    fun getPluginLoadingResults(): Map<String, Pair<ProjectHubPlugin?, Exception?>> {
        val results = mutableMapOf<String, Pair<ProjectHubPlugin?, Exception?>>()
        
        // Add loaded plugins
        plugins.forEach { (id, plugin) ->
            results[id] = Pair(plugin, null)
        }
        
        return results
    }
    
    fun reloadPlugins() {
        // Stop all active plugins first
        plugins.values.forEach { plugin ->
            if (lifecycleStates[plugin.id] == PluginLifecycleState.ACTIVE) {
                stopPlugin(plugin)
            }
        }
        
        // Clear all plugins
        plugins.clear()
        lifecycleStates.clear()
        
        // Discover plugins again
        discoverPlugins()
    }

    fun validateAndStartPlugins(session: Session) {
        plugins.values.forEach { plugin ->
            if (securityHub.validatePluginAccess(session, plugin.id)) {
                try {
                    startPlugin(plugin)
                    println("Started plugin ${plugin.id}")
                } catch (e: Exception) {
                    println("Failed to start plugin ${plugin.id}: ${e.message}")
                }
            } else {
                println("Access denied for plugin ${plugin.id}")
            }
        }
    }
}

/**
 * Descriptor for plugin metadata
 */
data class PluginDescriptor(
    val mainClass: String,
    val version: String,
    val author: String,
    val website: String
)
