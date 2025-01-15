package com.projecthub.base.school.infrastructure.specification;

import com.projecthub.base.school.domain.criteria.SchoolSearchCriteria;
import com.projecthub.base.school.domain.entity.School;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public enum SchoolSpecification {
    ;
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String ARCHIVED = "archived";
    private static final String CREATED_AT = "createdAt";

    public static Specification<School> withCriteria(final SchoolSearchCriteria criteria) {
        return Specification.where(SchoolSpecification.hasName(criteria.getName()))
            .and(SchoolSpecification.hasCity(criteria.getCity()))
            .and(SchoolSpecification.hasState(criteria.getState()))
            .and(SchoolSpecification.isArchived(criteria.getArchived()))
            .and(SchoolSpecification.createdAfter(criteria.getCreatedAfter()))
            .and(SchoolSpecification.createdBefore(criteria.getCreatedBefore()));
    }

    public static Specification<School> hasName(final String name) {
        return (root, _, cb) -> null == name ? null :
            cb.like(cb.lower(root.get(SchoolSpecification.NAME)), "%" + name.toLowerCase() + "%");
    }

    public static Specification<School> hasCity(final String city) {
        return (root, _, cb) -> {
            return null == city ? null :
                cb.equal(cb.lower(root.get(SchoolSpecification.ADDRESS).get(SchoolSpecification.CITY)), city.toLowerCase());
        };
    }

    public static Specification<School> hasState(final String state) {
        return (root, query, cb) -> {
            return null == state ? null :
                cb.equal(cb.lower(root.get(SchoolSpecification.ADDRESS).get(SchoolSpecification.STATE)), state.toLowerCase());
        };
    }

    public static Specification<School> isArchived(final Boolean archived) {
        return (root, query, cb) -> null == archived ? null :
            cb.equal(root.get(SchoolSpecification.ARCHIVED), archived);
    }

    public static Specification<School> createdAfter(final LocalDateTime date) {
        return (root, query, cb) -> {
            return null == date ? null :
                cb.greaterThanOrEqualTo(root.get(SchoolSpecification.CREATED_AT), date);
        };
    }

    public static Specification<School> createdBefore(final LocalDateTime date) {
        return (root, query, cb) -> {
            return null == date ? null :
                cb.lessThanOrEqualTo(root.get(SchoolSpecification.CREATED_AT), date);
        };
    }
}
