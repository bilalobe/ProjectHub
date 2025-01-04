package com.projecthub.base.cohort.application.service;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.cohort.api.mapper.CohortMapper;
import com.projecthub.base.cohort.application.port.in.CreateCohortUseCase;
import com.projecthub.base.cohort.application.port.in.UpdateCohortUseCase;
import com.projecthub.base.cohort.application.port.out.LoadCohortPort;
import com.projecthub.base.cohort.application.port.out.SaveCohortPort;
import com.projecthub.base.cohort.domain.command.CreateCohortCommand;
import com.projecthub.base.cohort.domain.command.DeleteCohortCommand;
import com.projecthub.base.cohort.domain.command.UpdateCohortCommand;
import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.cohort.domain.event.CohortEventPublisher;
import com.projecthub.base.cohort.domain.validation.CohortValidator;
import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.school.domain.repository.SchoolRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CohortCommandService implements CreateCohortUseCase, UpdateCohortUseCase {
    private final LoadCohortPort loadCohortPort;
    private final SaveCohortPort saveCohortPort;
    private final CohortValidator cohortValidator;
    private final CohortEventPublisher eventPublisher;
    private final CohortMapper cohortMapper;
    private final SchoolRepository schoolRepository;

    @Override
    @Transactional
    public CohortDTO createCohort(CreateCohortCommand command) {
        log.debug("Creating new cohort from command");

        School school = getSchoolById(command.getSchoolId());
        Cohort cohort = new Cohort();

        cohort.setName(command.getName());
        cohort.setStartTerm(command.getStartTerm());
        cohort.setEndTerm(command.getEndTerm());
        cohort.assignToSchool(school, command.getYear(), command.getLevel(), command.getMaxStudents());

        cohortValidator.validateCreate(cohort, school);

        Cohort savedCohort = saveCohortPort.save(cohort);
        eventPublisher.publishCreated(savedCohort);

        log.info("Created cohort with ID: {}", savedCohort.getId());
        return cohortMapper.toDto(savedCohort);
    }

    @Override
    @Transactional
    public CohortDTO updateCohort(UpdateCohortCommand command) {
        log.debug("Updating cohort with ID: {}", command.getId());

        Cohort existingCohort = loadCohortPort.loadCohortById(command.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + command.getId()));
        School school = existingCohort.getSchool();

        existingCohort.setName(command.getName());
        existingCohort.updateDetails(command.getStartTerm(), command.getEndTerm(), cohortValidator);
        existingCohort.updateMaxStudents(command.getMaxStudents(), cohortValidator);

        cohortValidator.validateUpdate(existingCohort, school);

        Cohort updatedCohort = saveCohortPort.save(existingCohort);
        eventPublisher.publishUpdated(updatedCohort);

        return cohortMapper.toDto(updatedCohort);
    }


    @Transactional
    public void deleteCohort(DeleteCohortCommand command) {
        log.debug("Deleting cohort with ID: {}", command.getId());

        Cohort cohort = loadCohortPort.loadCohortById(command.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + command.getId()));

        if (command.getReason() != null) {
            cohort.archive(command.getReason(), cohortValidator);
            saveCohortPort.save(cohort);
        } else {
            saveCohortPort.delete(cohort);
        }

        eventPublisher.publishDeleted(command.getId());
    }

    private School getSchoolById(UUID schoolId) {
        return schoolRepository.findById(schoolId)
            .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));
    }
}
