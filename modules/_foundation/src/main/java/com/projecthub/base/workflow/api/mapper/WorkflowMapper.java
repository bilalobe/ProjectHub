package com.projecthub.base.workflow.api.mapper;

import com.projecthub.base.workflow.api.dto.*;
import com.projecthub.base.workflow.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WorkflowMapper {
    public WorkflowDefinitionDTO toDto(WorkflowDefinition workflow) {
        return WorkflowDefinitionDTO.builder()
            .id(workflow.getId())
            .name(workflow.getName())
            .description(workflow.getDescription())
            .states(workflow.getStates().stream().map(this::toDto).toList())
            .transitions(workflow.getTransitions().stream().map(this::toDto).toList())
            .initialStateId(workflow.getInitialState().getId())
            .build();
    }

    public List<WorkflowDefinitionDTO> toDtoList(List<WorkflowDefinition> workflows) {
        return workflows.stream()
            .map(this::toDto)
            .toList();
    }

    public WorkflowStateDTO toDto(WorkflowState state) {
        return WorkflowStateDTO.builder()
            .id(state.getId())
            .name(state.getName())
            .description(state.getDescription())
            .category(state.getCategory())
            .build();
    }

    public WorkflowTransitionDTO toDto(WorkflowTransition transition) {
        return WorkflowTransitionDTO.builder()
            .id(transition.getId())
            .name(transition.getName())
            .description(transition.getDescription())
            .fromStateId(transition.getFromState().getId())
            .toStateId(transition.getToState().getId())
            .build();
    }

    public WorkflowDefinition toEntity(WorkflowDefinitionDTO dto) {
        // First create all states
        List<WorkflowState> states = dto.getStates().stream()
            .map(this::toEntity)
            .toList();

        // Create a map for easy lookup
        Map<UUID, WorkflowState> stateMap = states.stream()
            .collect(Collectors.toMap(WorkflowState::getId, Function.identity()));

        // Create transitions using the state map
        List<WorkflowTransition> transitions = dto.getTransitions().stream()
            .map(transitionDto -> toEntity(transitionDto, stateMap))
            .toList();

        return WorkflowDefinition.builder()
            .id(dto.getId())
            .name(dto.getName())
            .description(dto.getDescription())
            .states(states)
            .transitions(transitions)
            .initialState(stateMap.get(dto.getInitialStateId()))
            .build();
    }

    public WorkflowState toEntity(WorkflowStateDTO dto) {
        return WorkflowState.builder()
            .id(dto.getId())
            .name(dto.getName())
            .description(dto.getDescription())
            .category(dto.getCategory())
            .build();
    }

    public WorkflowTransition toEntity(WorkflowTransitionDTO dto, Map<UUID, WorkflowState> stateMap) {
        return WorkflowTransition.builder()
            .id(dto.getId())
            .name(dto.getName())
            .description(dto.getDescription())
            .fromState(stateMap.get(dto.getFromStateId()))
            .toState(stateMap.get(dto.getToStateId()))
            .actions(List.of()) // Actions will be configured separately
            .validators(List.of()) // Validators will be configured separately
            .build();
    }

    public WorkflowContext toContext(WorkflowTransitionRequestDTO request) {
        return WorkflowContext.builder()
            .entityId(request.getEntityId())
            .entityType(request.getEntityType())
            .currentState(WorkflowState.builder().id(request.getCurrentStateId()).build())
            .targetState(WorkflowState.builder().id(request.getTargetStateId()).build())
            .metadata(request.getMetadata())
            .build();
    }
}