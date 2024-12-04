package com.projecthub.repository.csv;

import com.projecthub.model.AppUser;
import java.util.Optional;

import org.springframework.stereotype.Repository;


import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Repository interface for {@link AppUser} entities.
 */
@Repository("userCsvRepository")
@Profile("csv")
@Primary

public interface AppUserCsvRepository extends BaseCsvRepository<AppUser> {

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return an {@code Optional} containing the user if found, or empty if not found
     */
    Optional<AppUser> findByUsername(String username);
}