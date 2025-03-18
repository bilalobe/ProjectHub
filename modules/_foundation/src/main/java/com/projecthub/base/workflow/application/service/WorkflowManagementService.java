package com.projecthub.base.workflow.application.service;

import com.projecthub.base.workflow.domain.model.WorkflowDefinition;
import com.projecthub.base.workflow.domain.model.WorkflowContext;
import com.projecthub.base.workflow.domain.repository.WorkflowDefinitionRepository;
import com.projecthub.base.workflow.domain.service.WorkflowEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowManagementService {
    private final WorkflowDefinitionRepository workflowRepository;
    private final WorkflowEngine workflowEngine;

    @Transactional(readOnly = true)
    public List<WorkflowDefinition> getAllWorkflows() {
        return workflowRepository.findAll();
    }

    @Transactional(readOnly = true)
    public WorkflowDefinition getWorkflowById(UUID id) {
        return workflowRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Workflow not found"));
    }

    @Transactional
    public WorkflowDefinition createWorkflow(WorkflowDefinition workflow) {
        // Basic validation
        if (workflow.getStates().isEmpty()) {
            throw new IllegalArgumentException("Workflow must have at least one state");
        }
        if (workflow.getInitialState() == null) {
            throw new IllegalArgumentException("Workflow must have an initial state");
        }

        return workflowRepository.save(workflow);
    }

    @Transactional
    public void executeTransition(UUID workflowId, WorkflowContext context) {
        WorkflowDefinition workflow = getWorkflowById(workflowId);
        workflowEngine.transition(workflow, context);
    }

    @Transactional
    public void deleteWorkflow(UUID id) {
        workflowRepository.deleteById(id);
    }
}