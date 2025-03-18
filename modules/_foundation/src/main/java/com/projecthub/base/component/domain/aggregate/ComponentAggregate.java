package com.projecthub.base.component.domain.aggregate;


import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AggregateRoot
public class ComponentAggregate {
    private final Component root;
    private final List<ComponentDomainEvent> events;
    private final ComponentEventPublisher eventPublisher;
    private final UUID initiatorId;

    private ComponentAggregate(final Component root, final ComponentEventPublisher eventPublisher, final UUID initiatorId) {
        this.root = root;
        this.eventPublisher = eventPublisher;
        this.initiatorId = initiatorId;
        events = new ArrayList<>();
    }

    public static ComponentAggregate create(final CreateComponentCommand command, final ComponentEventPublisher eventPublisher) {
        final Component component = new Component(command.getName(), command.getDescription());
        final ComponentAggregate aggregate = new ComponentAggregate(component, eventPublisher, command.getInitiatorId());
        aggregate.registerCreated();
        return aggregate;
    }

    private void registerCreated() {
        this.eventPublisher.publishCreated(this.root, this.initiatorId);
    }

    public void update(final UpdateComponentCommand command) {
        this.root.update(command.getName(), command.getDescription());
        this.eventPublisher.publishUpdated(this.root, this.initiatorId);
    }

    public void delete() {
        this.eventPublisher.publishDeleted(this.root.getId(), this.initiatorId);
    }

    public List<ComponentDomainEvent> getDomainEvents() {
        return List.copyOf(this.events);
    }

    public void clearDomainEvents() {
        this.events.clear();
    }

    private void registerEvent(final ComponentDomainEvent event) {
        this.events.add(event);
        this.eventPublisher.publish(event);
    }
}
