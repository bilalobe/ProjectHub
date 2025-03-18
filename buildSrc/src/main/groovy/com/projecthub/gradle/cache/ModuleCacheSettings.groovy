package com.projecthub.gradle.cache

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.ListProperty

class ModuleCacheSettings {
    private final Project project
    private final MapProperty<String, List<String>> cacheablePatterns
    private final ListProperty<String> nonCacheableTasks
    private final MapProperty<String, String> inputNormalization
    
    ModuleCacheSettings(Project project) {
        this.project = project
        this.cacheablePatterns = project.objects.mapProperty(String, List)
        this.nonCacheableTasks = project.objects.listProperty(String)
        this.inputNormalization = project.objects.mapProperty(String, String)
        
        initializeCachePatterns()
        initializeNonCacheableTasks()
        initializeInputNormalization()
    }
    
    private void initializeCachePatterns() {
        cacheablePatterns.set([
            'common': [
                'compileJava',
                'compileKotlin',
                'compileTestJava',
                'compileTestKotlin',
                'processResources',
                'processTestResources',
                'jar',
                'test',
                'spotlessCheck',
                'spotlessApply'
            ],
            'mobile': [
                'bundle*',
                'compile*AndroidResources',
                'compile*Sources',
                'merge*Resources',
                'process*Resources',
                'generate*BuildConfig',
                'package*',
                'lint*'
            ],
            'angular': [
                'npmInstall',
                'angularBuild*',
                'angularTest',
                'angularLint'
            ],
            'documentation': [
                'asciidoctor',
                'generatePdf',
                'generateHtml',
                'docToolchain*'
            ],
            'spring': [
                'bootJar',
                'bootWar',
                'bootBuildImage',
                'springBoot*'
            ]
        ])
    }
    
    private void initializeNonCacheableTasks() {
        nonCacheableTasks.set([
            'clean',
            'wrapper',
            'dependencies',
            'dependencyUpdates',
            'sonarqube',
            'angularServe',
            'npmAudit'
        ])
    }
    
    private void initializeInputNormalization() {
        inputNormalization.set([
            'text': '**/*.{java,kt,groovy,xml,properties,yml,yaml,json,js,ts,html,css,scss}',
            'timestamp': '**/*',
            'path': '**/*.{gradle,gradle.kts,properties}'
        ])
    }
    
    Provider<Map<String, List<String>>> getCacheableTaskPatterns() {
        return cacheablePatterns
    }
    
    Provider<List<String>> getNonCacheableTasks() {
        return nonCacheableTasks
    }
    
    Provider<Map<String, String>> getInputNormalization() {
        return inputNormalization
    }
    
    void configureCaching() {
        project.normalization {
            runtimeClasspath {
                // Configure input normalization for build cache
                metaInf {
                    ignoreAttribute('Implementation-Version')
                    ignoreAttribute('Build-Time')
                    ignoreAttribute('Build-Timestamp')
                }
            }
        }
        
        // Apply task-specific caching configuration
        project.tasks.configureEach { task ->
            if (shouldCacheTask(task.name)) {
                task.outputs.cacheIf { true }
            }
        }
    }
    
    private boolean shouldCacheTask(String taskName) {
        // Check if task is in non-cacheable list
        if (nonCacheableTasks.get().any { pattern -> 
            taskName.startsWith(pattern) || taskName.matches(pattern)
        }) {
            return false
        }
        
        // Check if task matches any cacheable pattern
        return cacheablePatterns.get().values().flatten().any { pattern ->
            taskName.startsWith(pattern.replace('*', '')) || 
            (pattern.contains('*') && taskName.matches(pattern.replace('*', '.*')))
        }
    }
}