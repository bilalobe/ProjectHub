package com.projecthub.base.team.domain.event;

import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
public class TeamAssignedToCohortEvent {
    UUID teamId;
    UUID cohortId;
    Instant occurredOn;
}
