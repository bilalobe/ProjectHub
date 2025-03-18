package com.projecthub.base.project.infrastructure.service;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.project.infrastructure.mapper.ProjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectSearchService {
    
    private final EntityManager entityManager;
    private final ProjectMapper projectMapper;

    public Page<ProjectDTO> searchProjects(ProjectSearchCriteria criteria, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);
        Root<Project> project = query.from(Project.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        if (criteria.getName() != null) {
            predicates.add(cb.like(cb.lower(project.get("name")), 
                "%" + criteria.getName().toLowerCase() + "%"));
        }
        
        if (criteria.getStatus() != null) {
            predicates.add(cb.equal(project.get("status"), criteria.getStatus()));
        }
        
        if (criteria.getTeamId() != null) {
            predicates.add(cb.equal(project.get("teamId"), criteria.getTeamId()));
        }
        
        if (criteria.getStartDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(
                project.get("startDate"), criteria.getStartDateFrom()));
        }
        
        if (criteria.getStartDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(
                project.get("startDate"), criteria.getStartDateTo()));
        }
        
        if (criteria.getDeadlineFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(
                project.get("deadline"), criteria.getDeadlineFrom()));
        }
        
        if (criteria.getDeadlineTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(
                project.get("deadline"), criteria.getDeadlineTo()));
        }
        
        // Add sort criteria
        if (criteria.getSortField() != null) {
            if (criteria.isAscending()) {
                query.orderBy(cb.asc(project.get(criteria.getSortField())));
            } else {
                query.orderBy(cb.desc(project.get(criteria.getSortField())));
            }
        }

        query.where(predicates.toArray(new Predicate[0]));
        
        List<Project> projects = entityManager.createQuery(query)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();
            
        // Get total count for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Project> projectCount = countQuery.from(Project.class);
        countQuery.select(cb.count(projectCount))
            .where(predicates.toArray(new Predicate[0]));
            
        Long total = entityManager.createQuery(countQuery).getSingleResult();
        
        List<ProjectDTO> projectDTOs = projects.stream()
            .map(projectMapper::toDto)
            .toList();
            
        return new PageImpl<>(projectDTOs, pageable, total);
    }
    
    public record ProjectSearchCriteria(
        String name,
        ProjectStatus status,
        UUID teamId,
        LocalDate startDateFrom,
        LocalDate startDateTo,
        LocalDate deadlineFrom,
        LocalDate deadlineTo,
        String sortField,
        boolean ascending
    ) {}
}