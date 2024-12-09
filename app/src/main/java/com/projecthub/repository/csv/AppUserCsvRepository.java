package com.projecthub.repository.csv;

import com.projecthub.model.AppUser;
import com.projecthub.repository.AppUserRepository;
import java.util.Optional;

/**
 * Repository interface for {@link AppUser} entities.
 */
public interface AppUserCsvRepository extends BaseCsvRepository<AppUser>, AppUserRepository {


    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return an {@code Optional} containing the user if found, or empty if not found
     */
    Optional<AppUser> findByUsername(String username);
}