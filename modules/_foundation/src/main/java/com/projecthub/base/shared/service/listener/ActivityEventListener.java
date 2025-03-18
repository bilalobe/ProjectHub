package com.projecthub.base.shared.service.listener;

import com.projecthub.base.shared.domain.event.ActivityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ActivityEventListener {
    private static final Logger logger = LoggerFactory.getLogger(ActivityEventListener.class);

    // TODO: Inject activity feed repository/service once created
    
    @EventListener
    public void handleActivityEvent(ActivityEvent event) {
        // For now, just log the activity. In a real implementation, we would:
        // 1. Save the activity to the activity feed database
        // 2. Update activity counters/statistics
        // 3. Trigger any necessary notifications
        // 4. Update search indices if needed
        logger.info("Activity recorded: {} performed {} on {} ({})",
            event.userId(),
            event.activityType(),
            event.entityType(),
            event.entityId()
        );
    }
}