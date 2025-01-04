package com.projecthub.base.cohort.domain.event;

public interface CohortEventAdapter {
    void publish(CohortDomainEvent event);

    String getRoutingKey(CohortDomainEvent event);

    String getExchange();
}
