package com.projecthub.base.cohort.infrastructure.persistence;

import com.projecthub.base.cohort.application.port.out.LoadCohortPort;
import com.projecthub.base.cohort.application.port.out.SaveCohortPort;
import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.school.cohort.application.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class CohortPersistenceAdapter implements LoadCohortPort, SaveCohortPort {
    private final CohortRepository cohortRepository;

    @Override
    public Optional<Cohort> loadCohortById(UUID id) {
        return cohortRepository.findById(id);
    }

    @Override
    public Page<Cohort> loadAllCohorts(Pageable pageable) {
        return cohortRepository.findAll(pageable);
    }

    @Override
    public Page<Cohort> loadCohortsBySchool(UUID schoolId, Pageable pageable) {
        return cohortRepository.findBySchoolId(schoolId, pageable);
    }

    @Override
    public Cohort save(Cohort cohort) {
        return cohortRepository.save(cohort);
    }

    @Override
    public void delete(Cohort cohort) {
        cohortRepository.deleteById(UUID);
    }
}
