package com.projecthub.base.student.infrastructure.messaging;

import com.projecthub.base.student.domain.event.StudentDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StudentEventListener {

    public StudentEventListener() {
    }

    @RabbitListener(queues = StudentMessagingConfig.STUDENT_CREATED_QUEUE)
    public static void handleStudentCreated(StudentDomainEvent.Created event) {
        log.info("Handling student created event: {}", event);
        // Add any specific handling logic here
    }

    @RabbitListener(queues = StudentMessagingConfig.STUDENT_UPDATED_QUEUE)
    public static void handleStudentUpdated(StudentDomainEvent.Updated event) {
        log.info("Handling student updated event: {}", event);
        // Add any specific handling logic here
    }

    @RabbitListener(queues = StudentMessagingConfig.STUDENT_DELETED_QUEUE)
    public static void handleStudentDeleted(StudentDomainEvent.Deleted event) {
        log.info("Handling student deleted event: {}", event);
        // Add any specific handling logic here
    }
}
