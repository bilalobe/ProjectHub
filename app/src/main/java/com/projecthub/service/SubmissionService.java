package com.projecthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.Submission;
import com.projecthub.repository.custom.CustomSubmissionRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Service
@Tag(name = "Submission Service", description = "Operations pertaining to submissions in ProjectHub")
public class SubmissionService {

    private final CustomSubmissionRepository submissionRepository;

    public SubmissionService(@Qualifier("csvSubmissionRepository") CustomSubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Operation(summary = "View a list of all submissions")
    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    @Operation(summary = "Save a submission")
    public Submission saveSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    @Operation(summary = "Delete a submission by ID")
    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }

    public List<Submission> getSubmissionsByStudentId(Long id) {
        return submissionRepository.findByStudentId(id);
    }
}