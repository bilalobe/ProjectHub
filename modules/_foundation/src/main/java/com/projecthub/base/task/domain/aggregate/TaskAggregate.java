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

    private TaskAggregate(final Task root, final TaskEventPublisher eventPublisher, final UUID initiatorId) {
        this.root = root;
        this.eventPublisher = eventPublisher;
        this.initiatorId = initiatorId;
        events = new ArrayList<>();
    }

    public static TaskAggregate create(final CreateTaskCommand command, final TaskEventPublisher eventPublisher) {
        final Task task = new Task(command.getName(), command.getDescription());
        final TaskAggregate aggregate = new TaskAggregate(task, eventPublisher, command.getInitiatorId());
        aggregate.registerCreated();
        return aggregate;
    }

    private void registerCreated() {
        this.eventPublisher.publishCreated(this.root, this.initiatorId);
    }

    public void update(final UpdateTaskCommand command) {
        this.root.update(command.getName(), command.getDescription());
        this.eventPublisher.publishUpdated(this.root, this.initiatorId);
    }

    public void delete() {
        this.eventPublisher.publishDeleted(this.root.getId(), this.initiatorId);
    }

    public List<TaskDomainEvent> getDomainEvents() {
        return List.copyOf(this.events);
    }

    public void clearDomainEvents() {
        this.events.clear();
    }

    private void registerEvent(final TaskDomainEvent event) {
        this.events.add(event);
        this.eventPublisher.publish(event);
    }
}
