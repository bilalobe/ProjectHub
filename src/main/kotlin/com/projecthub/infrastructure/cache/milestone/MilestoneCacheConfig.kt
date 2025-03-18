package com.projecthub.infrastructure.cache.milestone

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class MilestoneCacheConfig {

    companion object {
        const val MILESTONE_CACHE = "milestones"
        const val PROJECT_MILESTONES_CACHE = "project-milestones"
        const val UPCOMING_MILESTONES_CACHE = "upcoming-milestones"
    }

    @Bean
    fun milestoneCacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager()

        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats()
        )

        cacheManager.setCacheNames(
            setOf(
                MILESTONE_CACHE,
                PROJECT_MILESTONES_CACHE,
                UPCOMING_MILESTONES_CACHE
            )
        )

        return cacheManager
    }
}
