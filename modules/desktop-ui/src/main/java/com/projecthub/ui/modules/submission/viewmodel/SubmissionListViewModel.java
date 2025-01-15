package com.projecthub.ui.modules.submission.viewmodel;

import com.projecthub.base.submission.application.service.SubmissionService;
import com.projecthub.base.project.application.service.ProjectService;
import com.projecthub.base.submission.domain.dto.SubmissionDTO;
import com.projecthub.base.project.domain.dto.ProjectDTO;
import com.projecthub.base.submission.domain.enums.SubmissionStatus;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SubmissionListViewModel {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionListViewModel.class);
    private static final int PAGE_SIZE = 20;

    private final SubmissionService submissionService;
    private final ProjectService projectService;

    private final ObservableList<SubmissionDTO> submissions = FXCollections.observableArrayList();
    private final ObservableList<ProjectDTO> projects = FXCollections.observableArrayList();
    private final IntegerProperty pageCount = new SimpleIntegerProperty(0);

    private SubmissionStatus currentStatusFilter;
    private ProjectDTO currentProjectFilter;
    private String currentSearchTerm;
    private int currentPage;

    public SubmissionListViewModel(SubmissionService submissionService, ProjectService projectService) {
        this.submissionService = submissionService;
        this.projectService = projectService;
        loadProjects();
    }

    public void loadSubmissions() {
        try {
            Page<SubmissionDTO> page = submissionService.getSubmissions(
                buildFilters(), 
                PageRequest.of(currentPage, PAGE_SIZE)
            );
            submissions.setAll(page.getContent());
            pageCount.set(page.getTotalPages());
        } catch (Exception e) {
            logger.error("Failed to load submissions", e);
            // Handle error (show alert, etc.)
        }
    }

    private void loadProjects() {
        try {
            projects.setAll(projectService.getAllProjects());
        } catch (Exception e) {
            logger.error("Failed to load projects", e);
        }
    }

    public void filterByStatus(SubmissionStatus status) {
        this.currentStatusFilter = status;
        loadSubmissions();
    }

    public void filterByProject(ProjectDTO project) {
        this.currentProjectFilter = project;
        loadSubmissions();
    }

    public void filterBySearchTerm(String term) {
        this.currentSearchTerm = term;
        loadSubmissions();
    }

    public void loadPage(int page) {
        this.currentPage = page;
        loadSubmissions();
    }

    private Specification<Submission> buildFilters() {
        // Build JPA Specification based on current filters
        Specification<Submission> spec = Specification.where(null);
        
        if (currentStatusFilter != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("status"), currentStatusFilter));
        }
        
        if (currentProjectFilter != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("project").get("id"), currentProjectFilter.getId()));
        }
        
        if (currentSearchTerm != null && !currentSearchTerm.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("content")), "%" + currentSearchTerm.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("student").get("firstName")), "%" + currentSearchTerm.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("student").get("lastName")), "%" + currentSearchTerm.toLowerCase() + "%")
                ));
        }
        
        return spec;
    }

    // Getters for observable collections and properties
    public ObservableList<SubmissionDTO> getSubmissions() {
        return submissions;
    }

    public ObservableList<ProjectDTO> getProjects() {
        return projects;
    }

    public IntegerProperty pageCountProperty() {
        return pageCount;
    }
}
