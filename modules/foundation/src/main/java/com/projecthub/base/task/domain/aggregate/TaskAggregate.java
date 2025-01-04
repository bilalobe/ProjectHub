package com.projecthub.base.task.domain.aggregate;

import com.projecthub.base.task.domain.entity.Task;
import com.projecthub.base.task.domain.event.TaskDomainEvent;
import com.projecthub.base.task.domain.event.TaskEventPublisher;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AggregateRoot
public class TaskAggregate {
    private final Task root;
    private final List<TaskDomainEvent> events;
    private final TaskEventPublisher eventPublisher;
    private final UUID initiatorId;

    private TaskAggregate(Task root, TaskEventPublisher eventPublisher, UUID initiatorId) {
        this.root = root;
        this.eventPublisher = eventPublisher;
        this.initiatorId = initiatorId;
        this.events = new ArrayList<>();
    }

    public static TaskAggregate create(CreateTaskCommand command, TaskEventPublisher eventPublisher) {
        Task task = new Task(command.getName(), command.getDescription());
        TaskAggregate aggregate = new TaskAggregate(task, eventPublisher, command.getInitiatorId());
        aggregate.registerCreated();
        return aggregate;
    }

    private void registerCreated() {
        eventPublisher.publishCreated(root, initiatorId);
    }

    public void update(UpdateTaskCommand command) {
        root.update(command.getName(), command.getDescription());
        eventPublisher.publishUpdated(root, initiatorId);
    }

    public void delete() {
        eventPublisher.publishDeleted(root.getId(), initiatorId);
    }

    public List<TaskDomainEvent> getDomainEvents() {
        return List.copyOf(events);
    }

    public void clearDomainEvents() {
        events.clear();
    }

    private void registerEvent(TaskDomainEvent event) {
        events.add(event);
        eventPublisher.publish(event);
    }
}
