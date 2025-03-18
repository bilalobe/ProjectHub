package com.projecthub.base.auth.domain.entity;

import com.projecthub.base.shared.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.jetbrains.annotations.NonNls;

import java.util.Locale;

/**
 * Entity representing a permission in the RBAC system.
 * <p>
 * Permissions define specific operations that can be performed within the application.
 * These permissions are designed to integrate with Apache Fortress and implement
 * fine-grained access control for the user management module.
 * </p>
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {

    @NotBlank(message = "Permission name is mandatory")
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String objectType;

    @Column(nullable = false)
    private String operation;

    /**
     * Create a new permission with the specified name.
     *
     * @param name The permission name
     */
    public Permission(String name) {
        this.name = name;
    }

    /**
     * Create a new permission with the specified name and description.
     *
     * @param name The permission name
     * @param description The permission description
     */
    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Helper method to create a user management related permission.
     *
     * @param operation The operation to perform (create, read, update, delete)
     * @return A new Permission instance
     */
    public static Permission forUserManagement(@NonNls @NonNls String operation) {
        return Permission.builder()
            .name("USER_MANAGEMENT_" + operation.toUpperCase(Locale.ROOT))
            .description("Permission to " + operation + " users")
            .objectType("USER")
            .operation(operation.toUpperCase(Locale.ROOT))
            .build();
    }
}
