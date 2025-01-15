package com.projecthub.base.submission.infrastructure.persistence;

import com.projecthub.base.submission.application.port.out.SubmissionPort;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.infrastructure.repository.SubmissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class SubmissionPersistenceAdapter implements SubmissionPort {

    private final SubmissionJpaRepository jpaRepository;

    @Override
    public Submission save(Submission submission) {
        return jpaRepository.save(submission);
    }

    @Override
    public Optional<Submission> findById(UUID id) {
        return jpaRepository.findById(id);
    }
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    @Override
    public Page<Submission> findAll(Specification<Submission> spec, Pageable pageable) {
        return jpaRepository.findAll(spec, pageable);
    }
}