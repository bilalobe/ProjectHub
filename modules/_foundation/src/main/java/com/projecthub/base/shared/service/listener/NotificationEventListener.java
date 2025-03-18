package com.projecthub.base.shared.service.listener;

import com.projecthub.base.shared.domain.event.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {
    private static final Logger logger = LoggerFactory.getLogger(NotificationEventListener.class);

    // TODO: Inject notification service/repository once created
    
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        // For now, just log the notification. In a real implementation, we would:
        // 1. Store the notification in the database
        // 2. Send push notifications if configured
        // 3. Send emails if configured
        // 4. Update notification counters
        logger.info("Notification created: {} for recipient {} from {}",
            event.type(),
            event.recipientId(),
            event.senderId()
        );
    }
}