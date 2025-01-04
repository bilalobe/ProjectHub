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

    private ComponentAggregate(Component root, ComponentEventPublisher eventPublisher, UUID initiatorId) {
        this.root = root;
        this.eventPublisher = eventPublisher;
        this.initiatorId = initiatorId;
        this.events = new ArrayList<>();
    }

    public static ComponentAggregate create(CreateComponentCommand command, ComponentEventPublisher eventPublisher) {
        Component component = new Component(command.getName(), command.getDescription());
        ComponentAggregate aggregate = new ComponentAggregate(component, eventPublisher, command.getInitiatorId());
        aggregate.registerCreated();
        return aggregate;
    }

    private void registerCreated() {
        eventPublisher.publishCreated(root, initiatorId);
    }

    public void update(UpdateComponentCommand command) {
        root.update(command.getName(), command.getDescription());
        eventPublisher.publishUpdated(root, initiatorId);
    }

    public void delete() {
        eventPublisher.publishDeleted(root.getId(), initiatorId);
    }

    public List<ComponentDomainEvent> getDomainEvents() {
        return List.copyOf(events);
    }

    public void clearDomainEvents() {
        events.clear();
    }

    private void registerEvent(ComponentDomainEvent event) {
        events.add(event);
        eventPublisher.publish(event);
    }
}
