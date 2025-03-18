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
    public CohortDTO createCohort(final CreateCohortCommand command) {
        CohortCommandService.log.debug("Creating new cohort from command");

        final School school = this.getSchoolById(command.getSchoolId());
        final Cohort cohort = new Cohort();

        cohort.setName(command.getName());
        cohort.setStartTerm(command.getStartTerm());
        cohort.setEndTerm(command.getEndTerm());
        cohort.assignToSchool(school, command.getYear(), command.getLevel(), command.getMaxStudents());

        this.cohortValidator.validateCreate(cohort, school);

        final Cohort savedCohort = this.saveCohortPort.save(cohort);
        this.eventPublisher.publishCreated(savedCohort);

        CohortCommandService.log.info("Created cohort with ID: {}", savedCohort.getId());
        return this.cohortMapper.toDto(savedCohort);
    }

    @Override
    @Transactional
    public CohortDTO updateCohort(final UpdateCohortCommand command) {
        CohortCommandService.log.debug("Updating cohort with ID: {}", command.getId());

        final Cohort existingCohort = this.loadCohortPort.loadCohortById(command.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + command.getId()));
        final School school = existingCohort.getSchool();

        existingCohort.setName(command.getName());
        existingCohort.updateDetails(command.getStartTerm(), command.getEndTerm(), this.cohortValidator);
        existingCohort.updateMaxStudents(command.getMaxStudents(), this.cohortValidator);

        this.cohortValidator.validateUpdate(existingCohort, school);

        final Cohort updatedCohort = this.saveCohortPort.save(existingCohort);
        this.eventPublisher.publishUpdated(updatedCohort);

        return this.cohortMapper.toDto(updatedCohort);
    }


    @Transactional
    public void deleteCohort(final DeleteCohortCommand command) {
        CohortCommandService.log.debug("Deleting cohort with ID: {}", command.getId());

        final Cohort cohort = this.loadCohortPort.loadCohortById(command.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + command.getId()));

        if (null != command.getReason()) {
            cohort.archive(command.getReason(), this.cohortValidator);
            this.saveCohortPort.save(cohort);
        } else {
            this.saveCohortPort.delete(cohort);
        }

        this.eventPublisher.publishDeleted(command.getId());
    }

    private School getSchoolById(final UUID schoolId) {
        return this.schoolRepository.findById(schoolId)
            .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));
    }
}
