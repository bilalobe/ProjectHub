package com.projecthub.base.project.infrastructure.service;

import com.projecthub.base.notification.api.NotificationChannel;
import com.projecthub.base.notification.api.NotificationPriority;
import com.projecthub.base.notification.service.NotificationService;
import com.projecthub.base.project.domain.entity.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectNotificationService {
    
    private final NotificationService notificationService;

    public void sendOverdueNotification(Project project) {
        String message = String.format("Project '%s' is overdue. Original deadline was %s", 
            project.getName(), project.getDeadline());
            
        notificationService.sendNotification(
            project.getTeam().getId(),
            "Project Overdue",
            message,
            NotificationPriority.HIGH,
            NotificationChannel.EMAIL,
            Map.of(
                "projectId", project.getId(),
                "projectName", project.getName(),
                "daysOverdue", ChronoUnit.DAYS.between(project.getDeadline(), LocalDate.now())
            )
        );
    }

    public void sendDeadlineReminderNotification(Project project) {
        long daysUntilDeadline = ChronoUnit.DAYS.between(LocalDate.now(), project.getDeadline());
        String message = String.format("Project '%s' deadline approaching in %d days", 
            project.getName(), daysUntilDeadline);
            
        notificationService.sendNotification(
            project.getTeam().getId(),
            "Deadline Reminder",
            message,
            NotificationPriority.MEDIUM,
            NotificationChannel.EMAIL,
            Map.of(
                "projectId", project.getId(),
                "projectName", project.getName(),
                "daysRemaining", daysUntilDeadline
            )
        );
    }

    public void sendProjectArchivedNotification(Project project) {
        String message = String.format("Project '%s' has been archived", project.getName());
            
        notificationService.sendNotification(
            project.getTeam().getId(),
            "Project Archived",
            message,
            NotificationPriority.LOW,
            NotificationChannel.EMAIL,
            Map.of(
                "projectId", project.getId(),
                "projectName", project.getName(),
                "archiveDate", LocalDate.now()
            )
        );
    }

    public void sendStatusChangeNotification(Project project, String oldStatus, String newStatus) {
        String message = String.format("Project '%s' status changed from %s to %s", 
            project.getName(), oldStatus, newStatus);
            
        notificationService.sendNotification(
            project.getTeam().getId(),
            "Project Status Change",
            message,
            NotificationPriority.MEDIUM,
            NotificationChannel.EMAIL,
            Map.of(
                "projectId", project.getId(),
                "projectName", project.getName(),
                "oldStatus", oldStatus,
                "newStatus", newStatus
            )
        );
    }
}