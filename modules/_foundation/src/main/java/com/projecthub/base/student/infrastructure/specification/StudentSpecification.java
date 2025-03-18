package com.projecthub.base.student.infrastructure.specification;

import com.projecthub.base.shared.domain.enums.status.ActivationStatus;
import com.projecthub.base.student.domain.entity.Student;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NonNls;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class StudentSpecification {

    private StudentSpecification() {
        // Utility class
    }

    public static Specification<Student> withDynamicQuery(
        @NonNls String searchTerm,
        UUID teamId,
        ActivationStatus status,
        LocalDate enrolledAfter,
        LocalDate enrolledBefore) {

        return new Specification<Student>() {
            @Override
            public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    @NonNls @NonNls String pattern = "%" + searchTerm.toLowerCase(Locale.ROOT) + "%";
                    predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern)
                    ));
                }

                if (teamId != null) {
                    predicates.add(cb.equal(root.get("team").get("id"), teamId));
                }

                if (status != null) {
                    predicates.add(cb.equal(root.get("status"), status));
                }

                if (enrolledAfter != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("enrollmentDate"), enrolledAfter));
                }

                if (enrolledBefore != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("enrollmentDate"), enrolledBefore));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    public static Specification<Student> byTeamId(UUID teamId) {
        return new Specification<Student>() {
            @Override
            public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return teamId == null ? null : cb.equal(root.get("team").get("id"), teamId);
            }
        };
    }

    public static Specification<Student> byStatus(ActivationStatus status) {
        return new Specification<Student>() {
            @Override
            public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return status == null ? null : cb.equal(root.get("status"), status);
            }
        };
    }

    public static Specification<Student> byEnrollmentDateRange(LocalDate start, LocalDate end) {
        return new Specification<Student>() {
            @Override
            public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (start == null && end == null) return null;

                if (start == null) {
                    return cb.lessThanOrEqualTo(root.get("enrollmentDate"), end);
                }

                if (end == null) {
                    return cb.greaterThanOrEqualTo(root.get("enrollmentDate"), start);
                }

                return cb.between(root.get("enrollmentDate"), start, end);
            }
        };
    }

    public static Specification<Student> searchByTerm(@NonNls String term) {
        return new Specification<Student>() {
            @Override
            public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (term == null || term.trim().isEmpty()) return null;

                @NonNls @NonNls String pattern = "%" + term.toLowerCase(Locale.ROOT) + "%";
                return cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
                );
            }
        };
    }
}
