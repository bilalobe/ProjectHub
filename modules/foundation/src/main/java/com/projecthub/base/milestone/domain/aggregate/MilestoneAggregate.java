package com.projecthub.base.milestone.domain.aggregate;

import com.projecthub.base.milestone.domain.command.CreateMilestoneCommand;
import com.projecthub.base.milestone.domain.command.UpdateMilestoneCommand;
import com.projecthub.base.milestone.domain.entity.Milestone;
import com.projecthub.base.milestone.domain.event.MilestoneDomainEvent;
import com.projecthub.base.milestone.domain.event.MilestoneEventPublisher;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AggregateRoot
public class MilestoneAggregate {
    private final Milestone root;
    private final List<MilestoneDomainEvent> events;
    private final MilestoneEventPublisher eventPublisher;
    private final UUID initiatorId;

    private MilestoneAggregate(Milestone root, MilestoneEventPublisher eventPublisher, UUID initiatorId) {
        this.root = root;
        this.eventPublisher = eventPublisher;
        this.initiatorId = initiatorId;
        this.events = new ArrayList<>();
    }

    public static MilestoneAggregate create(CreateMilestoneCommand command, MilestoneEventPublisher eventPublisher) {
        Milestone milestone = new Milestone(command.getName(), command.getDueDate());
        MilestoneAggregate aggregate = new MilestoneAggregate(milestone, eventPublisher, command.getInitiatorId());
        aggregate.registerCreated();
        return aggregate;
    }

    private void registerCreated() {
        eventPublisher.publishCreated(root, initiatorId);
    }

    public void update(UpdateMilestoneCommand command) {
        root.update(command.getName(), command.getDueDate());
        eventPublisher.publishUpdated(root, initiatorId);
    }

    public void delete() {
        eventPublisher.publishDeleted(root.getId(), initiatorId);
    }

    public List<MilestoneDomainEvent> getDomainEvents() {
        return List.copyOf(events);
    }

    public void clearDomainEvents() {
        events.clear();
    }
}
