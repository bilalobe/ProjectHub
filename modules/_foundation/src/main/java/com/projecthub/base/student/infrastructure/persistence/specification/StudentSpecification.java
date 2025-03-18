package com.projecthub.base.student.infrastructure.persistence.specification;

import com.projecthub.base.student.domain.entity.Student;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import org.jetbrains.annotations.NonNls;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentSpecification {

    public StudentSpecification() {
    }

    public static Specification<Student> withDynamicQuery(StudentSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getNameSearch() != null) {
                @NonNls @NonNls String searchTerm = "%" + criteria.getNameSearch().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("firstName")), searchTerm),
                    cb.like(cb.lower(root.get("lastName")), searchTerm)
                ));
            }

            if (criteria.getEmail() != null) {
                predicates.add(cb.equal(root.get("email"), criteria.getEmail()));
            }

            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            if (criteria.getTeamId() != null) {
                predicates.add(cb.equal(root.get("team").get("id"), criteria.getTeamId()));
            }

            if (criteria.getEnrollmentDateStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("enrollmentDate"), criteria.getEnrollmentDateStart()));
            }

            if (criteria.getEnrollmentDateEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("enrollmentDate"), criteria.getEnrollmentDateEnd()));
            }

            if (query instanceof CriteriaQuery<?>) {
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
