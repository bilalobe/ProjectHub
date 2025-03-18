package com.projecthub.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

/**
 * Android/Compose mobile application convention plugin
 * Provides standardized configuration for mobile UI modules
 */
class MobileConventionPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // Apply Android and Kotlin plugins
        project.plugins.apply('com.android.application')
        project.plugins.apply('org.jetbrains.kotlin.android')
        project.plugins.apply('org.jetbrains.compose')
        project.plugins.apply('dagger.hilt.android.plugin')
        project.plugins.apply('org.jetbrains.kotlin.kapt')
        project.plugins.apply('org.jetbrains.kotlin.plugin.compose')
        
        // Configure Android-specific settings
        configureAndroid(project)
        configureKotlinCompilation(project)
        configureTestExecution(project)
        
        // Apply common mobile dependencies
        applyMobileDependencies(project)
    }
    
    private void configureAndroid(Project project) {
        project.android {
            namespace = "com.projecthub.${project.name.replace('-', '.')}"
            compileSdk = 34
            
            defaultConfig {
                applicationId = "com.projecthub.${project.name.replace('-', '.')}"
                minSdk = 23
                targetSdk = 34
                versionCode = 1
                versionName = project.version.toString()
                vectorDrawables {
                    useSupportLibrary = true
                }
                
                // Configure test instrumentation runner
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
            
            buildFeatures {
                compose = true
                buildConfig = true
            }
            
            composeOptions {
                kotlinCompilerExtensionVersion = project.rootProject.versions.compose
            }
            
            buildTypes {
                release {
                    minifyEnabled = true 
                    shrinkResources = true 
                    proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
                }
            }
            
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
                
                // Enable incremental compilation
                incremental = true
                
                // Configure worker API usage
                kotlinOptions {
                    jvmTarget = '17'
                    freeCompilerArgs += [
                        '-opt-in=kotlin.RequiresOptIn',
                        '-Xjvm-default=all'
                    ]
                }
            }
            
            testOptions {
                unitTests {
                    includeAndroidResources = true
                    all {
                        maxParallelForks = Runtime.runtime.availableProcessors().intdiv(2) ?: 1
                    }
                }
            }
            
            // Configure build cache
            buildCache {
                enabled = true
            }
        }
    }
    
    private void configureKotlinCompilation(Project project) {
        project.tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).configureEach { task ->
            task.kotlinOptions {
                jvmTarget = '17'
                freeCompilerArgs += [
                    '-opt-in=kotlin.RequiresOptIn',
                    '-Xjvm-default=all'
                ]
            }
            
            // Enable worker API for Kotlin compilation
            task.workers.max = Runtime.runtime.availableProcessors()
            task.workers.withWorkerHeapSize('2g')
        }
    }
    
    private void configureTestExecution(Project project) {
        project.tasks.withType(Test).configureEach { task ->
            task.maxParallelForks = Runtime.runtime.availableProcessors().intdiv(2) ?: 1
            task.failFast = false
            
            // Enable test output caching
            task.outputs.cacheIf { true }
            
            // Configure test JVM args
            task.jvmArgs += [
                '--add-opens=java.base/java.lang=ALL-UNNAMED',
                '--add-opens=java.base/java.util=ALL-UNNAMED'
            ]
        }
    }
    
    private void applyMobileDependencies(Project project) {
        project.dependencies {
            // Standard foundation dependency
            implementation project.project(':modules:foundation')
            
            // Compose dependencies
            implementation platform("androidx.compose:compose-bom:${project.rootProject.versions.composeBom}")
            implementation 'androidx.compose.ui:ui'
            implementation 'androidx.compose.material3:material3'
            implementation 'androidx.compose.ui:ui-tooling-preview'
            debugImplementation 'androidx.compose.ui:ui-tooling'
            
            // Navigation
            implementation "androidx.navigation:navigation-compose:${project.rootProject.versions.navigationCompose}"
            
            // Dependency Injection
            implementation "com.google.dagger:hilt-android:${project.rootProject.versions.hilt}"
            kapt "com.google.dagger:hilt-compiler:${project.rootProject.versions.hilt}"
            implementation "androidx.hilt:hilt-navigation-compose:${project.rootProject.versions.hiltNavigation}"
            
            // Android Architecture Components
            implementation "androidx.lifecycle:lifecycle-runtime-compose:${project.rootProject.versions.lifecycleRuntime}"
            implementation "androidx.lifecycle:lifecycle-viewmodel-compose:${project.rootProject.versions.lifecycleRuntime}"
            implementation "androidx.lifecycle:lifecycle-runtime-ktx:${project.rootProject.versions.lifecycleRuntime}"
            
            // Coroutines
            implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:${project.rootProject.versions.kotlinCoroutines}"
            
            // Testing
            testImplementation 'junit:junit:4.13.2'
            testImplementation 'androidx.test.ext:junit:1.1.5'
            testImplementation 'androidx.test.espresso:espresso-core:3.5.1'
            testImplementation "androidx.compose.ui:ui-test-junit4:${project.rootProject.versions.compose}"
        }
    }
}