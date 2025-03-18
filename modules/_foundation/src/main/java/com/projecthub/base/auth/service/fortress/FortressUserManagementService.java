package com.projecthub.base.auth.service.fortress;

import com.projecthub.base.auth.api.dto.RoleDTO;
import com.projecthub.base.auth.api.dto.UserCreateDTO;
import com.projecthub.base.auth.api.dto.UserDTO;
import com.projecthub.base.auth.api.dto.UserUpdateDTO;
import org.apache.directory.fortress.core.AdminMgr;
import org.apache.directory.fortress.core.DelAdminMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.*;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing users via Apache Fortress.
 * Provides functionality for creating, reading, updating, and deleting users,
 * as well as managing their role assignments and constraints.
 */
@Service
public class FortressUserManagementService {

    private static final Logger log = LoggerFactory.getLogger(FortressUserManagementService.class);

    private final AdminMgr adminManager;
    private final ReviewMgr reviewManager;
    private final DelAdminMgr delAdminManager;
    private final FortressPasswordService passwordService;

    @Autowired
    public FortressUserManagementService(
            AdminMgr adminManager,
            ReviewMgr reviewManager,
            DelAdminMgr delAdminManager,
            FortressPasswordService passwordService) {
        this.adminManager = adminManager;
        this.reviewManager = reviewManager;
        this.delAdminManager = delAdminManager;
        this.passwordService = passwordService;
    }

    /**
     * Get a paginated list of all users.
     *
     * @param pageable pagination information
     * @return a page of users
     */
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        try {
            // Retrieve all users (Fortress doesn't support pagination natively)
            List<User> users = reviewManager.findUsers(new User("*"));

            // Manual pagination
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), users.size());

            List<User> pageContent = users.subList(start, end);
            List<UserDTO> dtos = pageContent.stream()
                .map(this::mapToUserDTO)
                .collect(Collectors.toList());

            return new PageImpl<>(dtos, pageable, (long) users.size());
        } catch (RuntimeException e) {
            log.error("Error retrieving users: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve users", e);
        }
    }

    /**
     * Get a user by username.
     *
     * @param username the username to look up
     * @return the user DTO
     */
    public UserDTO getUserByUsername(@NonNls String username) {
        try {
            User user = reviewManager.readUser(new User(username));
            return mapToUserDTO(user);
        } catch (RuntimeException e) {
            log.error("Error retrieving user {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve user: " + username, e);
        }
    }

    /**
     * Create a new user with roles.
     *
     * @param createDTO the user creation data
     * @return the created user DTO
     */
    public UserDTO createUser(UserCreateDTO createDTO) {
        try {
            // Create base user object
            User user = new User();
            user.setUserId(UUID.randomUUID().toString());
            user.setName(createDTO.username());
            user.setOu("People");

            // Set user information
            user.setDescription(createDTO.description());
            user.setCn(createDTO.fullName());

            // Set password with validation
            user = passwordService.updateUserPassword(user, createDTO.password());

            // Add user to Fortress
            adminManager.addUser(user);

            // Assign roles
            if (createDTO.roles() != null && !createDTO.roles().isEmpty()) {
                for (String roleName : createDTO.roles()) {
                    adminManager.assignUser(user, new Role(roleName));
                }
            }

            // Add temporal constraints if specified
            if (createDTO.validFrom() != null || createDTO.validTo() != null) {
                applyTemporalConstraints(user, createDTO.validFrom(), createDTO.validTo());
            }

            // Read the created user back to get all attributes
            User createdUser = reviewManager.readUser(new User(createDTO.username()));
            return mapToUserDTO(createdUser);

        } catch (Exception e) {
            log.error("Error creating user {}: {}", createDTO.username(), e.getMessage(), e);
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing user.
     *
     * @param username the username of the user to update
     * @param updateDTO the update data
     * @return the updated user DTO
     */
    public UserDTO updateUser(String username, UserUpdateDTO updateDTO) {
        try {
            // Retrieve existing user
            User user = reviewManager.readUser(new User(username));
            if (user == null) {
                throw new RuntimeException("User not found: " + username);
            }

            // Update fields
            if (updateDTO.fullName() != null) {
                user.setCn(updateDTO.fullName());
            }

            if (updateDTO.description() != null) {
                user.setDescription(updateDTO.description());
            }

            if (updateDTO.password() != null && !updateDTO.password().isEmpty()) {
                user = passwordService.updateUserPassword(user, updateDTO.password());
            }

            // Update user
            adminManager.updateUser(user);

            // Update roles if specified
            if (updateDTO.roles() != null) {
                // Get current roles
                Set<String> currentRoles = reviewManager.assignedRoles(user)
                    .stream()
                    .map(UserRole::getName)
                    .collect(Collectors.toSet());

                Set<String> newRoles = new HashSet<>(updateDTO.roles());

                // Roles to remove
                Collection<String> rolesToRemove = new HashSet<>(currentRoles);
                rolesToRemove.removeAll(newRoles);

                // Roles to add
                Collection<String> rolesToAdd = new HashSet<>(newRoles);
                rolesToAdd.removeAll(currentRoles);

                // Process removals
                for (String roleName : rolesToRemove) {
                    adminManager.deassignUser(user, new Role(roleName));
                }

                // Process additions
                for (String roleName : rolesToAdd) {
                    adminManager.assignUser(user, new Role(roleName));
                }
            }

            // Update temporal constraints if specified
            if (updateDTO.validFrom() != null || updateDTO.validTo() != null) {
                applyTemporalConstraints(user, updateDTO.validFrom(), updateDTO.validTo());
            }

            // Read the updated user
            User updatedUser = reviewManager.readUser(new User(username));
            return mapToUserDTO(updatedUser);

        } catch (RuntimeException e) {
            log.error("Error updating user {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a user.
     *
     * @param username the username of the user to delete
     */
    public void deleteUser(String username) {
        try {
            User user = new User(username);

            // Remove role assignments first
            List<UserRole> roles = reviewManager.assignedRoles(user);
            for (UserRole userRole : roles) {
                adminManager.deassignUser(user, new Role(userRole.getName()));
            }

            // Delete the user
            adminManager.deleteUser(user);
            log.info("User deleted: {}", username);

        } catch (RuntimeException e) {
            log.error("Error deleting user {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    /**
     * Lock a user account.
     *
     * @param username the username of the account to lock
     */
    public void lockUserAccount(String username) {
        try {
            User user = reviewManager.readUser(new User(username));
            user.setLocked(true);
            adminManager.updateUser(user);
            log.info("User account locked: {}", username);
        } catch (RuntimeException e) {
            log.error("Error locking user account {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to lock user account: " + e.getMessage(), e);
        }
    }

    /**
     * Unlock a user account.
     *
     * @param username the username of the account to unlock
     */
    public void unlockUserAccount(String username) {
        try {
            User user = reviewManager.readUser(new User(username));
            user.setLocked(false);
            user.setReset(0); // Reset failed login attempts
            adminManager.updateUser(user);
            log.info("User account unlocked: {}", username);
        } catch (RuntimeException e) {
            log.error("Error unlocking user account {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to unlock user account: " + e.getMessage(), e);
        }
    }

    /**
     * Get all roles assigned to a user.
     *
     * @param username the username to check
     * @return list of roles assigned to the user
     */
    public List<RoleDTO> getUserRoles(String username) {
        try {
            User user = new User(username);
            List<UserRole> userRoles = reviewManager.assignedRoles(user);

            return userRoles.stream()
                .map(this::mapToRoleDTO)
                .collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error("Error retrieving roles for user {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve user roles: " + e.getMessage(), e);
        }
    }

    /**
     * Get all available roles in the system.
     *
     * @return list of all roles
     */
    public List<RoleDTO> getAllRoles() {
        try {
            Role roleConstraint = new Role();
            roleConstraint.setName("*");
            List<Role> roles = reviewManager.findRoles(roleConstraint);

            return roles.stream()
                .map(role -> new RoleDTO(
                    role.getId(),
                    role.getName(),
                    role.getDescription(),
                    null, null))
                .collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error("Error retrieving roles: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve roles: " + e.getMessage(), e);
        }
    }

    /**
     * Apply temporal constraints to a user.
     *
     * @param user the user to apply constraints to
     * @param validFrom the start validity date
     * @param validTo the end validity date
     */
    private void applyTemporalConstraints(User user, ChronoLocalDateTime<LocalDate> validFrom, ChronoLocalDateTime<LocalDate> validTo)
            throws Exception {
        @NonNls @NonNls @NonNls @NonNls @NonNls @NonNls Constraint constraint = new Constraint();
        constraint.setId(UUID.randomUUID().toString());
        constraint.setType(ConstraintType.USER);

        // Set the timeout period - converted to seconds
        if (validFrom != null && validTo != null) {
            Date fromDate = Date.from(validFrom.atZone(ZoneId.systemDefault()).toInstant());
            Date toDate = Date.from(validTo.atZone(ZoneId.systemDefault()).toInstant());

            constraint.setBeginDate(fromDate.toString());
            constraint.setEndDate(toDate.toString());
            constraint.setBeginTime("00:00");
            constraint.setEndTime("23:59");

            // Blank separator means AND condition (both constraints must be satisfied)
            constraint.setRaw(constraint.getBeginDate() + " - " + constraint.getEndDate() + " & " +
                constraint.getBeginTime() + " - " + constraint.getEndTime());
        } else if (validFrom != null) {
            Date fromDate = Date.from(validFrom.atZone(ZoneId.systemDefault()).toInstant());
            constraint.setBeginDate(fromDate.toString());
            constraint.setRaw(constraint.getBeginDate() + " - ");
        } else if (validTo != null) {
            Date toDate = Date.from(validTo.atZone(ZoneId.systemDefault()).toInstant());
            constraint.setEndDate(toDate.toString());
            constraint.setRaw(" - " + constraint.getEndDate());
        }

        adminManager.addUserConstraint(user, constraint);
    }

    /**
     * Map a Fortress User to a UserDTO.
     *
     * @param user the Fortress user
     * @return the user DTO
     */
    private UserDTO mapToUserDTO(User user) {
        if (user == null) {
            return null;
        }

        List<UserRole> roles = Collections.emptyList();
        try {
            roles = reviewManager.assignedRoles(user);
        } catch (RuntimeException e) {
            log.warn("Error retrieving roles for user {}: {}", user.getName(), e.getMessage());
        }

        List<String> roleNames = roles.stream()
            .map(UserRole::getName)
            .collect(Collectors.toList());

        List<Constraint> constraints = new ArrayList<>();
        try {
            constraints = reviewManager.userConstraints(user);
        } catch (RuntimeException e) {
            log.warn("Error retrieving constraints for user {}: {}", user.getName(), e.getMessage());
        }

        LocalDateTime validFrom = null;
        LocalDateTime validTo = null;

        // Extract dates from constraints
        for (Constraint constraint : constraints) {
            if (constraint.getBeginDate() != null && !constraint.getBeginDate().isEmpty()) {
                try {
                    validFrom = parseConstraintDate(constraint.getBeginDate());
                } catch (RuntimeException e) {
                    log.warn("Failed to parse constraint begin date: {}", constraint.getBeginDate());
                }
            }

            if (constraint.getEndDate() != null && !constraint.getEndDate().isEmpty()) {
                try {
                    validTo = parseConstraintDate(constraint.getEndDate());
                } catch (RuntimeException e) {
                    log.warn("Failed to parse constraint end date: {}", constraint.getEndDate());
                }
            }
        }

        return new UserDTO(
            user.getUserId(),
            user.getName(),
            user.getCn(),
            user.getDescription(),
            validFrom,
            validTo,
            roleNames,
            user.isLocked(),
            user.getReset() > 0, // Reset count indicates failed logins
            user.getDaysMask(), // Days of week allowed
            user.getBeginTime(), // Daily time restriction start
            user.getEndTime(), // Daily time restriction end
            user.getTimeout() // Session timeout
        );
    }

    /**
     * Map a Fortress UserRole to a RoleDTO.
     *
     * @param userRole the Fortress user role
     * @return the role DTO
     */
    private RoleDTO mapToRoleDTO(UserRole userRole) {
        return new RoleDTO(
            userRole.getId(),
            userRole.getName(),
            null, // Description not available in UserRole
            parseConstraintDate(userRole.getBeginDate()),
            parseConstraintDate(userRole.getEndDate())
        );
    }

    /**
     * Parse a constraint date string into a LocalDateTime.
     *
     * @param dateString the date string
     * @return LocalDateTime object
     */
    private static LocalDateTime parseConstraintDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }

        try {
            // Assuming format like: "20221225" for Dec 25, 2022
            int year = Integer.parseInt(dateString.substring(0, 4));
            int month = Integer.parseInt(dateString.substring(4, 6));
            int day = Integer.parseInt(dateString.substring(6, 8));

            return LocalDateTime.of(year, month, day, 0, 0);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Failed to parse date string: {}", dateString, e);
            return null;
        }
    }
}
