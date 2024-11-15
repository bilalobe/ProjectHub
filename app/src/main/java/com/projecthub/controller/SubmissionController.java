package com.projecthub.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecthub.dto.SubmissionSummary;
import com.projecthub.model.Submission;
import com.projecthub.service.SubmissionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @GetMapping
    public List<SubmissionSummary> getAllSubmissions() {
        return submissionService.getAllSubmissions().stream()
                .map(SubmissionSummary::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public String createSubmission(@Valid @RequestBody Submission submission) {
        submissionService.saveSubmission(submission);
        return "Submission created successfully";
    }

    @DeleteMapping("/{id}")
    public String deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
        return "Submission deleted successfully";
    }
}