package com.projecthub.base.submission.infrastructure.event.store;

import com.projecthub.base.submission.domain.event.SubmissionEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventStore {
    private final List<SubmissionEvent> events = new ArrayList<>();

    public void save(SubmissionEvent event) {
        events.add(event);
    }

    public List<SubmissionEvent> getEvents() {
        return new ArrayList<>(events);
    }
}