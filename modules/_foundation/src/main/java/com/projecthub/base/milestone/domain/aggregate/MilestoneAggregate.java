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

    private MilestoneAggregate(final Milestone root, final MilestoneEventPublisher eventPublisher, final UUID initiatorId) {
        this.root = root;
        this.eventPublisher = eventPublisher;
        this.initiatorId = initiatorId;
        events = new ArrayList<>();
    }

    public static MilestoneAggregate create(final CreateMilestoneCommand command, final MilestoneEventPublisher eventPublisher) {
        final Milestone milestone = new Milestone(command.getName(), command.getDueDate());
        final MilestoneAggregate aggregate = new MilestoneAggregate(milestone, eventPublisher, command.getInitiatorId());
        aggregate.registerCreated();
        return aggregate;
    }

    private void registerCreated() {
        this.eventPublisher.publishCreated(this.root, this.initiatorId);
    }

    public void update(final UpdateMilestoneCommand command) {
        this.root.update(command.getName(), command.getDueDate());
        this.eventPublisher.publishUpdated(this.root, this.initiatorId);
    }

    public void delete() {
        this.eventPublisher.publishDeleted(this.root.getId(), this.initiatorId);
    }

    public List<MilestoneDomainEvent> getDomainEvents() {
        return List.copyOf(this.events);
    }

    public void clearDomainEvents() {
        this.events.clear();
    }
}
