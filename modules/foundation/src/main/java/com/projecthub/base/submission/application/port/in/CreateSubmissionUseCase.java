package com.projecthub.base.submission.application.port.in;

import com.projecthub.base.submission.domain.command.CreateSubmissionCommand;

public interface CreateSubmissionUseCase {
    SubmissionDTO createSubmission(CreateSubmissionCommand command);
}
