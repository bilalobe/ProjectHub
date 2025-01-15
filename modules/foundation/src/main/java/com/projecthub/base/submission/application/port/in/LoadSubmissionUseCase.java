package com.projecthub.base.submission.application.port.in;

import com.projecthub.base.submission.api.dto.SubmissionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LoadSubmissionUseCase {
    SubmissionDTO getSubmissionById(UUID id);

    List<SubmissionDTO> getAllSubmissions();

    Page<SubmissionDTO> getAllSubmissions(Pageable pageable);

    List<SubmissionDTO> getSubmissionsByProject(UUID projectId);

    Page<SubmissionDTO> getSubmissionsByProject(UUID projectId, Pageable pageable);

    List<SubmissionDTO> getSubmissionsByStudent(UUID studentId);

    Page<SubmissionDTO> getSubmissionsByStudent(UUID studentId, Pageable pageable);
}