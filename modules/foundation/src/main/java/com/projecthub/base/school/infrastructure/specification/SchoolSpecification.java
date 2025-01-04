package com.projecthub.base.school.infrastructure.specification;

import com.projecthub.base.school.domain.criteria.SchoolSearchCriteria;
import com.projecthub.base.school.domain.entity.School;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class SchoolSpecification {
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String ARCHIVED = "archived";
    private static final String CREATED_AT = "createdAt";

    private SchoolSpecification() {
    } // Prevent instantiation

    public static Specification<School> withCriteria(SchoolSearchCriteria criteria) {
        return Specification.where(hasName(criteria.getName()))
            .and(hasCity(criteria.getCity()))
            .and(hasState(criteria.getState()))
            .and(isArchived(criteria.getArchived()))
            .and(createdAfter(criteria.getCreatedAfter()))
            .and(createdBefore(criteria.getCreatedBefore()));
    }

    public static Specification<School> hasName(String name) {
        return (root, query, cb) -> {
            return name == null ? null :
                cb.like(cb.lower(root.get(NAME)), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<School> hasCity(String city) {
        return (root, query, cb) -> {
            return city == null ? null :
                cb.equal(cb.lower(root.get(ADDRESS).get(CITY)), city.toLowerCase());
        };
    }

    public static Specification<School> hasState(String state) {
        return (root, query, cb) -> {
            return state == null ? null :
                cb.equal(cb.lower(root.get(ADDRESS).get(STATE)), state.toLowerCase());
        };
    }

    public static Specification<School> isArchived(Boolean archived) {
        return (root, query, cb) -> {
            return archived == null ? null :
                cb.equal(root.get(ARCHIVED), archived);
        };
    }

    public static Specification<School> createdAfter(LocalDateTime date) {
        return (root, query, cb) -> {
            return date == null ? null :
                cb.greaterThanOrEqualTo(root.get(CREATED_AT), date);
        };
    }

    public static Specification<School> createdBefore(LocalDateTime date) {
        return (root, query, cb) -> {
            return date == null ? null :
                cb.lessThanOrEqualTo(root.get(CREATED_AT), date);
        };
    }
}
