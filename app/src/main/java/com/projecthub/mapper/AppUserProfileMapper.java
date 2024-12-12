package com.projecthub.mapper;

import com.projecthub.dto.AppUserProfileDTO;
import com.projecthub.model.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between AppUser and UserProfileDTO.
 * Provides methods to map between the entity and the DTO.
 */
@Mapper(componentModel = "spring")
public interface AppUserProfileMapper {

    /**
     * Converts an AppUser entity to a UserProfileDTO.
     *
     * @param user the AppUser entity to convert
     * @return the converted UserProfileDTO
     */
    AppUserProfileDTO toUserProfileDTO(AppUser user);

    /**
     * Updates an existing AppUser entity from a UserProfileDTO.
     *
     * @param userProfileDTO the UserProfileDTO containing updated profile details
     * @param user           the AppUser entity to update
     */
    void updateUserProfileFromDTO(AppUserProfileDTO userProfileDTO, @MappingTarget AppUser user);
}