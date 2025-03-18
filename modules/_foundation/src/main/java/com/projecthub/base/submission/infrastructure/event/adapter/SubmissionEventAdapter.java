package com.projecthub.base.submission.infrastructure.event;

import com.projecthub.base.submission.domain.event.SubmissionDomainEvent;

public interface SubmissionEventAdapter {
    void publish(SubmissionDomainEvent event);
    String getRoutingKey(SubmissionDomainEvent event);
    String getExchange();
}