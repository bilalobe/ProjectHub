package com.projecthub.base.task.infrastructure.event;

import com.projecthub.base.shared.infrastructure.event.BaseAsyncEventPublisher;
import com.projecthub.base.task.domain.event.TaskDomainEvent;
import com.projecthub.base.task.domain.event.TaskEventPublisher;
import com.projecthub.base.task.infrastructure.event.config.TaskRabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncTaskEventAdapter extends BaseAsyncEventPublisher<TaskDomainEvent> implements TaskEventPublisher {

    public AsyncTaskEventAdapter(final RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate, "taskEventExecutor");
    }

    @Override
    public String getExchange() {
        return TaskRabbitMQConfig.TASK_EXCHANGE;
    }

    @Override
    public String getRoutingKey(final TaskDomainEvent event) {
        return switch (event) {
            case TaskDomainEvent.Created _ -> TaskRabbitMQConfig.TASK_CREATED_KEY;
            case TaskDomainEvent.Updated _ -> TaskRabbitMQConfig.TASK_UPDATED_KEY;
            case TaskDomainEvent.Deleted _ -> TaskRabbitMQConfig.TASK_DELETED_KEY;
            case TaskDomainEvent.StatusChanged _ -> TaskRabbitMQConfig.TASK_STATUS_CHANGED_KEY;
        };
    }
}