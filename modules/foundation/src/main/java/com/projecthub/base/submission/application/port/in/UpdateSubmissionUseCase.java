package com.projecthub.base.submission.application.port.in;

import com.projecthub.base.submission.api.dto.SubmissionDTO;
import com.projecthub.base.submission.domain.command.UpdateSubmissionCommand;
import java.util.UUID;

public interface UpdateSubmissionUseCase {
    SubmissionDTO updateSubmission(UpdateSubmissionCommand command);
}
