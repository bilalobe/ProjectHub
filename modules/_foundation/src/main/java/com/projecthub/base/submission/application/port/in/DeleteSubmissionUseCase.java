package com.projecthub.base.submission.application.port.in;

import java.util.UUID;

public interface DeleteSubmissionUseCase {
    void deleteSubmission(UUID id, UUID initiatorId);
}
