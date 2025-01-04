package com.projecthub.base.team.domain.command;

import lombok.Value;

import java.util.UUID;

@Value
public class AssignTeamToCohortCommand {
    UUID teamId;
    UUID cohortId;
}
