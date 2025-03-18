package com.projecthub.security

import com.projecthub.plugin.ProjectHubPlugin
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader
import java.security.MessageDigest
import java.util.jar.JarFile

/**
 * Validates plugins for security concerns
 */
class PluginSecurityValidator(
    private val allowedPackages: List<String> = listOf("com.projecthub"),
    private val trustedSignatures: List<String> = emptyList()
) {
    private val logger = LoggerFactory.getLogger(PluginSecurityValidator::class.java)
    
    /**
     * Validate a plugin for security concerns
     */
    fun validatePlugin(plugin: ProjectHubPlugin): Boolean {
        // Check if the plugin class is in an allowed package
        val packageName = plugin.javaClass.`package`.name
        if (!isAllowedPackage(packageName)) {
            logger.warn("Plugin ${plugin.id} is in disallowed package $packageName")
            return false
        }
        
        // Check if the plugin is from a JAR and validate signature if so
        val classLoader = plugin.javaClass.classLoader
        if (classLoader is URLClassLoader) {
            val urls = classLoader.urLs
            for (url in urls) {
                if (url.protocol == "file" && url.path.endsWith(".jar")) {
                    val file = File(url.path)
                    if (!validateJarSignature(file)) {
                        logger.warn("Plugin ${plugin.id} JAR signature validation failed")
                        return false
                    }
                }
            }
        }
        
        // Check for required permissions
        val requiredPermissions = plugin.getRequiredPermissions()
        if (requiredPermissions.any { permission -> !isAllowedPermission(permission) }) {
            logger.warn("Plugin ${plugin.id} requests disallowed permissions")
            return false
        }
        
        return true
    }
    
    private fun isAllowedPackage(packageName: String): Boolean {
        return allowedPackages.any { allowed -> packageName.startsWith(allowed) }
    }
    
    private fun isAllowedPermission(permission: String): Boolean {
        // Implement permission validation logic
        // For example, block permissions that affect system security
        val disallowedPrefixes = listOf("system:", "admin:", "security:")
        return disallowedPrefixes.none { prefix -> permission.startsWith(prefix) }
    }
    
    private fun validateJarSignature(jarFile: File): Boolean {
        try {
            // Simple signature check for demonstration
            // In a real app, use proper JAR signature verification
            val jar = JarFile(jarFile, true)
            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(jarFile.readBytes())
            val signature = hash.joinToString("") { "%02x".format(it) }
            
            if (trustedSignatures.isEmpty()) {
                // If no trusted signatures defined, log a warning but allow
                logger.warn("No trusted signatures defined, allowing plugin JAR ${jarFile.name}")
                return true
            }
            
            return trustedSignatures.contains(signature)
        } catch (e: Exception) {
            logger.error("Error validating JAR signature for ${jarFile.name}", e)
            return false
        }
    }
}
