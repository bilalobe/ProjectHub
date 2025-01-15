package com.projecthub.base.submission.application.service;

import com.projecthub.base.milestone.domain.exception.MilestoneNotFoundException;
import com.projecthub.base.submission.api.dto.SubmissionDTO;
import com.projecthub.base.submission.api.mapper.SubmissionMapper;
import com.projecthub.base.submission.application.port.in.LoadSubmissionUseCase;
import com.projecthub.base.submission.application.port.out.SubmissionPort;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.infrastructure.specification.SubmissionSpecification;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.student.domain.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SubmissionQueryService implements LoadSubmissionUseCase {
    private final SubmissionPort submissionPort;
    private final SubmissionMapper submissionMapper;
    private final StudentRepository studentRepository;


    @Override
    public SubmissionDTO getSubmissionById(UUID id) {
        log.debug("Fetching submission by ID: {}", id);
      return submissionPort.findById(id)
           .map(submissionMapper::toDto)
            .orElseThrow(() -> new MilestoneNotFoundException("Submission not found for id:" + id));
    }

    @Override
    public List<SubmissionDTO> getAllSubmissions() {
        log.debug("Retrieving all submissions");
      return submissionPort.findAll().stream()
          .map(submissionMapper::toDto)
          .toList();
    }

    @Override
    public Page<SubmissionDTO> getAllSubmissions(Pageable pageable) {
         log.debug("Fetching all submissions with pagination");
         return submissionPort.findAll(pageable)
             .map(submissionMapper::toDto);
    }

      @Override
    public List<SubmissionDTO> getSubmissionsByProject(UUID projectId) {
         log.debug("Fetching submission by project ID: {}", projectId);
        return submissionPort.findAll(SubmissionSpecification.byProject(projectId)).stream()
          .map(submissionMapper::toDto)
            .toList();
    }


    @Override
    public Page<SubmissionDTO> getSubmissionsByProject(UUID projectId, Pageable pageable) {
          log.debug("Fetching paginated submissions by project: {} with page: {} and size: {}", projectId, pageable.getPageNumber(), pageable.getPageSize());
         return submissionPort.findAll(SubmissionSpecification.byProject(projectId), pageable)
            .map(submissionMapper::toDto);
    }

    @Override
    public List<SubmissionDTO> getSubmissionsByStudent(UUID studentId) {
        log.debug("Fetching all submissions for student with ID: {}", studentId);
        return submissionPort.findAll(SubmissionSpecification.byStudent(studentId)).stream()
            .map(submissionMapper::toDto)
            .toList();
    }

    @Override
    public Page<SubmissionDTO> getSubmissionsByStudent(UUID studentId, Pageable pageable) {
         log.debug("Fetching paginated submissions for student: {} with page: {} and size: {}", studentId, pageable.getPageNumber(), pageable.getPageSize());
          return submissionPort.findAll(SubmissionSpecification.byStudent(studentId), pageable)
            .map(submissionMapper::toDto);
    }
    private Submission findSubmissionById(UUID id) {
        return submissionPort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + id));
    }
   private Student getStudentById(UUID studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
    }
}