package com.projecthub.base.cohort.domain.event;

import com.projecthub.base.cohort.domain.entity.Cohort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CohortEventPublisher {
    private final CohortEventAdapter eventAdapter;

    public void publishCreated(Cohort cohort) {
        log.debug("Publishing cohort created event for cohort: {}", cohort.getId());
        CohortDomainEvent event = new CohortDomainEvent.Created(
            UUID.randomUUID(),
            cohort.getId(),
            cohort.getSchool().getId(),
            cohort.getName(),
            cohort.getAssignment().maxStudents(),
            getCurrentUserId(),
            Instant.now()
        );
        eventAdapter.publish(event);
    }

    public void publishUpdated(Cohort cohort) {
        log.debug("Publishing cohort updated event for cohort: {}", cohort.getId());
        CohortDomainEvent event = new CohortDomainEvent.Updated(
            UUID.randomUUID(),
            cohort.getId(),
            cohort.getName(),
            cohort.getAssignment().maxStudents(),
            getCurrentUserId(),
            Instant.now()
        );
        eventAdapter.publish(event);
    }

    public void publishDeleted(UUID cohortId) {
        log.debug("Publishing cohort deleted event for cohort: {}", cohortId);
        CohortDomainEvent event = new CohortDomainEvent.Deleted(
            UUID.randomUUID(),
            cohortId,
            getCurrentUserId(),
            Instant.now()
        );
        eventAdapter.publish(event);
    }

    private UUID getCurrentUserId() {
        return UUID.randomUUID();
    }
}
