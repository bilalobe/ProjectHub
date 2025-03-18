package com.projecthub.base.cohort.application.service;

import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.cohort.domain.validation.CohortValidator;
import com.projecthub.base.cohort.domain.value.SeatingConfiguration;
import com.projecthub.base.security.permission.CohortPermissions;
import com.projecthub.base.security.service.SecurityAuditService;
import com.projecthub.base.security.service.SecurityService;
import com.projecthub.base.security.service.SeatingSecurityService;
import com.projecthub.base.shared.domain.enums.security.SecurityAuditAction;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.shared.exception.UnauthorizedException;
import com.projecthub.base.team.domain.entity.Team;
import com.projecthub.base.team.domain.value.TeamPosition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing and scheduling seating arrangements for team cohorts.
 * Supports multiple seating strategies: FIFO, CONFIDENCE_BASED, and MANUAL.
 * Enforces RBAC/ABAC permissions for seating management operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatingSchedulerService {

    private final CohortService cohortService;
    private final CohortValidator cohortValidator;
    private final SeatingSecurityService seatingSecurityService;
    private final SecurityAuditService securityAuditService;

    /**
     * Configure seating arrangement for a cohort.
     *
     * @param cohortId The ID of the cohort to configure
     * @param rows Number of rows in the seating grid
     * @param columns Number of columns in the seating grid
     * @param layoutType The type of layout (CLASSROOM, LAB, CONFERENCE, etc.)
     * @param assignmentStrategy The strategy for assigning seats (MANUAL, FIFO, CONFIDENCE_BASED)
     * @return The updated cohort with seating configuration
     */
    @Transactional
    public Cohort configureSeating(UUID cohortId, int rows, int columns, 
                                   String layoutType, String assignmentStrategy) {
        log.debug("Configuring seating for cohort: {}", cohortId);
        
        // Enforce RBAC security check for configuring seating
        seatingSecurityService.enforceConfigureSeating(cohortId);
        
        Cohort cohort = cohortService.getCohortById(cohortId)
            .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + cohortId));
        
        // Create or update seating configuration
        SeatingConfiguration seatingConfig = new SeatingConfiguration(rows, columns, layoutType, assignmentStrategy);
        cohort.setSeatingConfiguration(seatingConfig);
        
        // Validate the cohort with the new configuration
        cohortValidator.validateUpdate(cohort, cohort.getSchool());
        
        // Save and audit the change
        Cohort savedCohort = cohortService.saveCohort(cohort);
        securityAuditService.logSecurityEvent(
            cohortId,
            SecurityAuditAction.PROFILE_UPDATE,
            "Seating configuration updated for cohort: " + cohortId
        );
        
        log.info("Seating configuration saved for cohort: {}", cohortId);
        return savedCohort;
    }
    
    /**
     * Configure custom seating layout for a cohort.
     * This is a restricted operation with higher permission requirements.
     *
     * @param cohortId The ID of the cohort to configure
     * @param customLayout Map of row indices to list of column indices representing available seats
     * @return The updated cohort with custom seating configuration
     */
    @Transactional
    public Cohort configureCustomSeating(UUID cohortId, Map<String, List<Integer>> customLayout) {
        log.debug("Configuring custom seating for cohort: {}", cohortId);
        
        // Higher permission check for custom layouts
        seatingSecurityService.enforceCreateCustomLayout();
        seatingSecurityService.enforceConfigureSeating(cohortId);
        
        Cohort cohort = cohortService.getCohortById(cohortId)
            .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + cohortId));
        
        // Get existing seating configuration or create new one
        SeatingConfiguration existingConfig = cohort.getSeatingConfiguration();
        SeatingConfiguration updatedConfig;
        
        if (existingConfig == null) {
            // Create a default configuration if none exists
            updatedConfig = new SeatingConfiguration(5, 5, "CUSTOM", "MANUAL");
        } else {
            updatedConfig = existingConfig;
            updatedConfig.setLayoutType("CUSTOM");
        }
        
        // Set custom layout
        updatedConfig.setCustomLayout(customLayout);
        cohort.setSeatingConfiguration(updatedConfig);
        
        // Save and audit the change
        Cohort savedCohort = cohortService.saveCohort(cohort);
        securityAuditService.logSecurityEvent(
            cohortId,
            SecurityAuditAction.PROFILE_UPDATE,
            "Custom seating layout configured for cohort: " + cohortId
        );
        
        log.info("Custom seating layout saved for cohort: {}", cohortId);
        return savedCohort;
    }
    
    /**
     * Apply a seating assignment strategy to automatically assign team positions.
     *
     * @param cohortId The ID of the cohort
     * @param strategy The strategy to apply (FIFO, CONFIDENCE_BASED, RANDOM)
     * @return Map of team IDs to their assigned positions
     */
    @Transactional
    public Map<UUID, TeamPosition> applySeatingStrategy(UUID cohortId, String strategy) {
        log.debug("Applying seating strategy '{}' for cohort: {}", strategy, cohortId);
        
        // Enforce RBAC security check for assigning seating
        seatingSecurityService.enforceAssignSeating(cohortId);
        
        Cohort cohort = cohortService.getCohortById(cohortId)
            .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + cohortId));
        
        if (cohort.getSeatingConfiguration() == null) {
            throw new IllegalStateException("Cohort does not have a seating configuration");
        }
        
        // Update the strategy in the configuration
        SeatingConfiguration config = cohort.getSeatingConfiguration();
        config.setAssignmentStrategy(strategy);
        cohort.setSeatingConfiguration(config);
        
        // Get teams for this cohort
        List<Team> teams = cohort.getTeams();
        
        // Apply the selected strategy to assign seating positions
        Map<UUID, TeamPosition> teamPositions = switch (strategy.toUpperCase()) {
            case "FIFO" -> assignSeatsByFIFO(cohort, teams);
            case "CONFIDENCE_BASED" -> assignSeatsByConfidence(cohort, teams);
            case "RANDOM" -> assignSeatsRandomly(cohort, teams);
            default -> throw new IllegalArgumentException("Unsupported seating strategy: " + strategy);
        };
        
        // Save the updated cohort configuration
        cohortService.saveCohort(cohort);
        
        // Audit the change
        securityAuditService.logSecurityEvent(
            cohortId,
            SecurityAuditAction.PROFILE_UPDATE,
            "Seating strategy '" + strategy + "' applied for cohort: " + cohortId
        );
        
        log.info("Seating strategy '{}' applied for cohort: {}", strategy, cohortId);
        return teamPositions;
    }
    
    /**
     * Manually assign a team to a seating position.
     *
     * @param cohortId The cohort ID
     * @param teamId The team ID
     * @param row Row position (0-based)
     * @param column Column position (0-based)
     * @return The updated team with new position
     */
    @Transactional
    public Team assignTeamPosition(UUID cohortId, UUID teamId, int row, int column) {
        log.debug("Assigning team {} to position ({}, {}) in cohort {}", teamId, row, column, cohortId);
        
        // Enforce RBAC security check for assigning seating
        seatingSecurityService.enforceAssignSeating(cohortId);
        
        Cohort cohort = cohortService.getCohortById(cohortId)
            .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + cohortId));
        
        // Validate that the position is within the seating grid
        SeatingConfiguration config = cohort.getSeatingConfiguration();
        if (config == null) {
            throw new IllegalStateException("Cohort does not have a seating configuration");
        }
        
        validatePosition(row, column, config);
        
        // Find the team and update its position
        Team team = cohort.getTeams().stream()
            .filter(t -> t.getId().equals(teamId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Team not found in cohort: " + teamId));
        
        // Check if position is already occupied
        boolean positionOccupied = cohort.getTeams().stream()
            .filter(t -> !t.getId().equals(teamId)) // Exclude the current team
            .anyMatch(t -> {
                TeamPosition pos = t.getPosition();
                return pos != null && pos.row() == row && pos.column() == column;
            });
        
        if (positionOccupied) {
            throw new IllegalStateException("Position (" + row + ", " + column + ") is already occupied");
        }
        
        // Update team position
        TeamPosition position = new TeamPosition(row, column, 
                                               team.getConfidenceScore(), 
                                               System.currentTimeMillis());
        team.setPosition(position);
        
        // Audit the change
        securityAuditService.logSecurityEvent(
            teamId,
            SecurityAuditAction.PROFILE_UPDATE,
            "Team position updated to (" + row + ", " + column + ") in cohort: " + cohortId
        );
        
        log.info("Team {} assigned to position ({}, {}) in cohort {}", teamId, row, column, cohortId);
        return team;
    }
    
    /**
     * Clear a team's seating position.
     *
     * @param cohortId The cohort ID
     * @param teamId The team ID
     * @return The updated team with position cleared
     */
    @Transactional
    public Team clearTeamPosition(UUID cohortId, UUID teamId) {
        log.debug("Clearing position for team {} in cohort {}", teamId, cohortId);
        
        // Enforce RBAC security check for assigning seating
        seatingSecurityService.enforceAssignSeating(cohortId);
        
        Cohort cohort = cohortService.getCohortById(cohortId)
            .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + cohortId));
        
        // Find the team and clear its position
        Team team = cohort.getTeams().stream()
            .filter(t -> t.getId().equals(teamId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Team not found in cohort: " + teamId));
        
        team.setPosition(null);
        
        // Audit the change
        securityAuditService.logSecurityEvent(
            teamId,
            SecurityAuditAction.PROFILE_UPDATE,
            "Team position cleared in cohort: " + cohortId
        );
        
        log.info("Position cleared for team {} in cohort {}", teamId, cohortId);
        return team;
    }
    
    /**
     * Check if a seating position is available.
     *
     * @param cohortId The cohort ID
     * @param row Row position
     * @param column Column position
     * @return true if position is available, false otherwise
     */
    public boolean isPositionAvailable(UUID cohortId, int row, int column) {
        log.debug("Checking availability of position ({}, {}) in cohort {}", row, column, cohortId);
        
        // Enforce RBAC security check for viewing seating
        seatingSecurityService.enforceViewSeating(cohortId);
        
        Cohort cohort = cohortService.getCohortById(cohortId)
            .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + cohortId));
        
        // Validate that the position is within bounds
        SeatingConfiguration config = cohort.getSeatingConfiguration();
        if (config == null) {
            throw new IllegalStateException("Cohort does not have a seating configuration");
        }
        
        try {
            validatePosition(row, column, config);
        } catch (IllegalArgumentException e) {
            return false; // Position is outside bounds
        }
        
        // Check if position is already occupied
        boolean positionOccupied = cohort.getTeams().stream()
            .anyMatch(t -> {
                TeamPosition pos = t.getPosition();
                return pos != null && pos.row() == row && pos.column() == column;
            });
        
        return !positionOccupied;
    }
    
    /**
     * Get seating configuration for a cohort.
     *
     * @param cohortId The cohort ID
     * @return The seating configuration
     */
    public SeatingConfiguration getSeatingConfiguration(UUID cohortId) {
        log.debug("Getting seating configuration for cohort: {}", cohortId);
        
        // Enforce RBAC security check for viewing seating
        seatingSecurityService.enforceViewSeating(cohortId);
        
        Cohort cohort = cohortService.getCohortById(cohortId)
            .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + cohortId));
        
        return cohort.getSeatingConfiguration();
    }
    
    /**
     * Get all team positions for a cohort.
     *
     * @param cohortId The cohort ID
     * @return Map of team IDs to their positions
     */
    public Map<UUID, TeamPosition> getTeamPositions(UUID cohortId) {
        log.debug("Getting team positions for cohort: {}", cohortId);
        
        // Enforce RBAC security check for viewing seating
        seatingSecurityService.enforceViewSeating(cohortId);
        
        Cohort cohort = cohortService.getCohortById(cohortId)
            .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + cohortId));
        
        // Build a map of team IDs to positions
        Map<UUID, TeamPosition> positions = new HashMap<>();
        for (Team team : cohort.getTeams()) {
            if (team.getPosition() != null) {
                positions.put(team.getId(), team.getPosition());
            }
        }
        
        return positions;
    }
    
    // Private helper methods for seating assignment
    
    private Map<UUID, TeamPosition> assignSeatsByFIFO(Cohort cohort, List<Team> teams) {
        log.debug("Assigning seats using FIFO strategy");
        
        SeatingConfiguration config = cohort.getSeatingConfiguration();
        int rows = config.getRows();
        int columns = config.getColumns();
        
        // Sort teams by creation date
        List<Team> sortedTeams = teams.stream()
            .sorted(Comparator.comparing(Team::getCreatedAt))
            .collect(Collectors.toList());
        
        Map<UUID, TeamPosition> positions = new HashMap<>();
        
        // Assign seats in row-major order
        int index = 0;
        for (Team team : sortedTeams) {
            if (index >= rows * columns) {
                log.warn("Not enough seats for all teams");
                break;
            }
            
            int row = index / columns;
            int col = index % columns;
            
            // Check if using custom layout
            if ("CUSTOM".equals(config.getLayoutType()) && config.getCustomLayout() != null) {
                // Find next available position in custom layout
                boolean foundPosition = false;
                while (index < rows * columns && !foundPosition) {
                    row = index / columns;
                    col = index % columns;
                    
                    String rowKey = String.valueOf(row);
                    List<Integer> availableCols = config.getCustomLayout().get(rowKey);
                    
                    if (availableCols != null && availableCols.contains(col)) {
                        foundPosition = true;
                    } else {
                        index++;
                    }
                }
                
                if (!foundPosition) {
                    log.warn("No available position in custom layout for team: {}", team.getId());
                    continue;
                }
            }
            
            TeamPosition position = new TeamPosition(row, col, 
                                                  team.getConfidenceScore(), 
                                                  System.currentTimeMillis());
            team.setPosition(position);
            positions.put(team.getId(), position);
            
            index++;
        }
        
        return positions;
    }
    
    private Map<UUID, TeamPosition> assignSeatsByConfidence(Cohort cohort, List<Team> teams) {
        log.debug("Assigning seats using confidence strategy");
        
        SeatingConfiguration config = cohort.getSeatingConfiguration();
        int rows = config.getRows();
        int columns = config.getColumns();
        
        // Sort teams by confidence score (descending)
        List<Team> sortedTeams = teams.stream()
            .sorted(Comparator.comparing(Team::getConfidenceScore).reversed())
            .collect(Collectors.toList());
        
        Map<UUID, TeamPosition> positions = new HashMap<>();
        
        // Assign seats from front to back based on confidence
        int index = 0;
        for (Team team : sortedTeams) {
            if (index >= rows * columns) {
                log.warn("Not enough seats for all teams");
                break;
            }
            
            int row = index / columns;
            int col = index % columns;
            
            // Check if using custom layout
            if ("CUSTOM".equals(config.getLayoutType()) && config.getCustomLayout() != null) {
                // Find next available position in custom layout
                boolean foundPosition = false;
                while (index < rows * columns && !foundPosition) {
                    row = index / columns;
                    col = index % columns;
                    
                    String rowKey = String.valueOf(row);
                    List<Integer> availableCols = config.getCustomLayout().get(rowKey);
                    
                    if (availableCols != null && availableCols.contains(col)) {
                        foundPosition = true;
                    } else {
                        index++;
                    }
                }
                
                if (!foundPosition) {
                    log.warn("No available position in custom layout for team: {}", team.getId());
                    continue;
                }
            }
            
            TeamPosition position = new TeamPosition(row, col, 
                                                  team.getConfidenceScore(), 
                                                  System.currentTimeMillis());
            team.setPosition(position);
            positions.put(team.getId(), position);
            
            index++;
        }
        
        return positions;
    }
    
    private Map<UUID, TeamPosition> assignSeatsRandomly(Cohort cohort, List<Team> teams) {
        log.debug("Assigning seats using random strategy");
        
        SeatingConfiguration config = cohort.getSeatingConfiguration();
        int rows = config.getRows();
        int columns = config.getColumns();
        
        // Shuffle teams randomly
        List<Team> shuffledTeams = new ArrayList<>(teams);
        Collections.shuffle(shuffledTeams);
        
        Map<UUID, TeamPosition> positions = new HashMap<>();
        
        // If using custom layout, get all available positions
        List<int[]> availablePositions = new ArrayList<>();
        if ("CUSTOM".equals(config.getLayoutType()) && config.getCustomLayout() != null) {
            config.getCustomLayout().forEach((rowKey, cols) -> {
                int rowNum = Integer.parseInt(rowKey);
                for (Integer col : cols) {
                    availablePositions.add(new int[]{rowNum, col});
                }
            });
            Collections.shuffle(availablePositions);
        }
        
        // Assign seats randomly
        int index = 0;
        for (Team team : shuffledTeams) {
            int row, col;
            
            if ("CUSTOM".equals(config.getLayoutType()) && config.getCustomLayout() != null) {
                if (index >= availablePositions.size()) {
                    log.warn("Not enough seats for all teams in custom layout");
                    break;
                }
                
                int[] position = availablePositions.get(index);
                row = position[0];
                col = position[1];
            } else {
                if (index >= rows * columns) {
                    log.warn("Not enough seats for all teams");
                    break;
                }
                
                row = index / columns;
                col = index % columns;
            }
            
            TeamPosition position = new TeamPosition(row, col, 
                                                  team.getConfidenceScore(), 
                                                  System.currentTimeMillis());
            team.setPosition(position);
            positions.put(team.getId(), position);
            
            index++;
        }
        
        return positions;
    }
    
    private void validatePosition(int row, int column, SeatingConfiguration config) {
        if (config == null) {
            throw new IllegalStateException("Seating configuration is null");
        }
        
        if (row < 0 || row >= config.getRows()) {
            throw new IllegalArgumentException("Row is outside the seating grid: " + row);
        }
        
        if (column < 0 || column >= config.getColumns()) {
            throw new IllegalArgumentException("Column is outside the seating grid: " + column);
        }
        
        // Check if using custom layout
        if ("CUSTOM".equals(config.getLayoutType()) && config.getCustomLayout() != null) {
            String rowKey = String.valueOf(row);
            List<Integer> availableCols = config.getCustomLayout().get(rowKey);
            
            if (availableCols == null || !availableCols.contains(column)) {
                throw new IllegalArgumentException("Position (" + row + ", " + column + ") is not available in the custom layout");
            }
        }
    }
}