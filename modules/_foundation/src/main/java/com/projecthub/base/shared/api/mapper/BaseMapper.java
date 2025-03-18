package com.projecthub.base.shared.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Base mapper interface providing common mapping operations.
 *
 * @param <D> DTO type
 * @param <E> Entity type
 */
public interface BaseMapper<D, E> {

    /**
     * Convert entity to DTO
     */
    D toDto(E entity);

    /**
     * Convert DTO to entity
     */
    E toEntity(D dto);

    /**
     * Update entity from DTO, ignoring null values
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(D dto, @MappingTarget E entity);
}
