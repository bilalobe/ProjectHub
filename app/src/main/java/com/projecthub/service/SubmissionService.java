package com.projecthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.Submission;
import com.projecthub.repository.custom.CustomSubmissionRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Service
@Api(value = "Submission Service", description = "Operations pertaining to submissions in ProjectHub")
public class SubmissionService {

    private final CustomSubmissionRepository submissionRepository;

    public SubmissionService(@Qualifier("csvSubmissionRepository") CustomSubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @ApiOperation(value = "View a list of all submissions", response = List.class)
    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    @ApiOperation(value = "Save a submission")
    public Submission saveSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    @ApiOperation(value = "Delete a submission by ID")
    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }
}