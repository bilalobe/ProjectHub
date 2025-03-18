package com.projecthub.base.cohort.infrastructure.persistence;

import com.projecthub.base.cohort.application.port.out.LoadCohortPort;
import com.projecthub.base.cohort.application.port.out.SaveCohortPort;
import com.projecthub.base.cohort.domain.entity.Cohort;
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
    public Optional<Cohort> loadCohortById(final UUID id) {
        return this.cohortRepository.findById(id);
    }

    @Override
    public Page<Cohort> loadAllCohorts(final Pageable pageable) {
        return this.cohortRepository.findAll(pageable);
    }

    @Override
    public Page<Cohort> loadCohortsBySchool(final UUID schoolId, final Pageable pageable) {
        return this.cohortRepository.findBySchoolId(schoolId, pageable);
    }

    @Override
    public Cohort save(final Cohort cohort) {
        return this.cohortRepository.save(cohort);
    }

    @Override
    public void delete(final Cohort cohort) {
        this.cohortRepository.delete(cohort);
    }
}
