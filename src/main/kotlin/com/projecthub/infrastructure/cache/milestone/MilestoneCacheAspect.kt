package com.projecthub.infrastructure.cache.milestone

import com.projecthub.domain.milestone.event.*
import com.projecthub.infrastructure.cache.milestone.MilestoneCacheConfig.Companion.MILESTONE_CACHE
import com.projecthub.infrastructure.cache.milestone.MilestoneCacheConfig.Companion.PROJECT_MILESTONES_CACHE
import com.projecthub.infrastructure.cache.milestone.MilestoneCacheConfig.Companion.UPCOMING_MILESTONES_CACHE
import org.springframework.cache.CacheManager
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class MilestoneCacheAspect(private val cacheManager: CacheManager) {

    @EventListener
    fun handleMilestoneCreatedEvent(event: MilestoneCreatedEvent) {
        // Evict project milestones cache for the affected project
        cacheManager.getCache(PROJECT_MILESTONES_CACHE)?.evict(event.projectId)
        // Evict upcoming milestones cache as it might be affected
        cacheManager.getCache(UPCOMING_MILESTONES_CACHE)?.clear()
    }

    @EventListener
    fun handleMilestoneCompletedEvent(event: MilestoneCompletedEvent) {
        // Evict individual milestone cache
        cacheManager.getCache(MILESTONE_CACHE)?.evict(event.milestoneId)
        // Evict project milestones cache
        cacheManager.getCache(PROJECT_MILESTONES_CACHE)?.evict(event.projectId)
        // Evict upcoming milestones cache
        cacheManager.getCache(UPCOMING_MILESTONES_CACHE)?.clear()
    }

    @EventListener
    fun handleMilestoneDueDateChangedEvent(event: MilestoneDueDateChangedEvent) {
        // Evict individual milestone cache
        cacheManager.getCache(MILESTONE_CACHE)?.evict(event.milestoneId)
        // Evict upcoming milestones cache as due date changed
        cacheManager.getCache(UPCOMING_MILESTONES_CACHE)?.clear()
    }

    @EventListener
    fun handleMilestoneAssignedEvent(event: MilestoneAssignedEvent) {
        // Evict individual milestone cache
        cacheManager.getCache(MILESTONE_CACHE)?.evict(event.milestoneId)
        // Evict project milestones cache as assignment changed
        cacheManager.getCache(PROJECT_MILESTONES_CACHE)?.evict(event.projectId)
    }

    @EventListener
    fun handleMilestoneUpdatedEvent(event: MilestoneUpdatedEvent) {
        // Evict individual milestone cache
        cacheManager.getCache(MILESTONE_CACHE)?.evict(event.milestoneId)
        // Evict project milestones cache
        cacheManager.getCache(PROJECT_MILESTONES_CACHE)?.evict(event.projectId)
        // Evict upcoming milestones cache as it might be affected by updates
        cacheManager.getCache(UPCOMING_MILESTONES_CACHE)?.clear()
    }
}
