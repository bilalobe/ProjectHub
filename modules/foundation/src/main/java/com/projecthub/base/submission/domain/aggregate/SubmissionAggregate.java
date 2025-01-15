package com.projecthub.base.submission.domain.aggregate;

import com.projecthub.base.submission.domain.command.CreateSubmissionCommand;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.event.SubmissionEvent;

import lombok.Getter;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.projecthub.base.submission.application.port.out.SubmissionEventPort;

@Getter
@AggregateRoot
public class SubmissionAggregate {
    private final Submission root;
    private final List<SubmissionEvent> events;
    private final SubmissionEventPort eventPublisher;
      private final UUID initiatorId;


    private SubmissionAggregate(Submission root, SubmissionEventPort eventPublisher, UUID initiatorId) {
        this.root = root;
        this.eventPublisher = eventPublisher;
         this.initiatorId = initiatorId;
        this.events = new ArrayList<>();
    }

     public static SubmissionAggregate create(CreateSubmissionCommand command, SubmissionEventPort eventPublisher) {
         Submission submission = Submission.builder()
            .content(command.getContent())
            .build();

         SubmissionAggregate aggregate = new SubmissionAggregate(submission, eventPublisher, command.getInitiatorId());
         aggregate.registerCreated();
        return aggregate;
    }

     private void registerCreated() {
          registerEvent(new SubmissionEvent.SubmissionCreated(
            UUID.randomUUID(),
            root.getId(),
             initiatorId,
            Instant.now()
        ));
    }

    public List<SubmissionEvent> getDomainEvents() {
        return List.copyOf(events);
    }

    public void clearDomainEvents() {
        events.clear();
    }

    public void update() {
         registerEvent(new SubmissionEvent.SubmissionUpdated(
            UUID.randomUUID(),
             root.getId(),
              initiatorId,
            Instant.now()
        ));
    }

    public void delete() {
         registerEvent(new SubmissionEvent.SubmissionDeleted(
            UUID.randomUUID(),
            root.getId(),
             initiatorId,
            Instant.now()
        ));
    }

    public void registerEvent(SubmissionEvent event) {
        events.add(Objects.requireNonNull(event));
    }

    public static SubmissionAggregateBuilder builder() {
        return new SubmissionAggregateBuilder();
    }

   public static class SubmissionAggregateBuilder {
        private Submission root;
         private SubmissionEventPort eventPublisher;
        private UUID initiatorId;

        public SubmissionAggregateBuilder root(Submission root) {
            this.root = root;
            return this;
        }

        public SubmissionAggregateBuilder eventPublisher(SubmissionEventPort eventPublisher) {
            this.eventPublisher = eventPublisher;
            return this;
        }

        public SubmissionAggregateBuilder initiatorId(UUID initiatorId) {
            this.initiatorId = initiatorId;
            return this;
        }

       public SubmissionAggregate build() {
           return new SubmissionAggregate(root, eventPublisher, initiatorId);
       }
   }
}