package com.projecthub.base.school.infrastructure.event.adapter;

import com.projecthub.base.school.domain.event.SchoolDomainEvent;

public interface SchoolEventAdapter {
    void publish(SchoolDomainEvent event);

    String getRoutingKey(SchoolDomainEvent event);

    String getExchange();
}
