package com.projecthub.base.user.domain.entity;

import com.projecthub.base.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

/**
 * Entity representing a user profile in the ProjectHub system.
 * <p>
 * This entity contains the user profile information and personal data.
 * Security and authentication concerns have been moved to {@link com.projecthub.base.auth.domain.entity.SecurityUser}.
 * </p>
 * <p>
 * Business rules:
 * <ul>
 *   <li>Each user must have a unique username</li>
 *   <li>Email addresses must be unique and valid</li>
 *   <li>Username and email are immutable after creation</li>
 * </ul>
 * </p>
 */
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    indexes = {
        @Index(name = "idx_user_username", columnList = "username"),
        @Index(name = "idx_user_email", columnList = "email")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_username", columnNames = "username"),
        @UniqueConstraint(name = "uc_user_email", columnNames = "email")
    }
)
@ToString(exclude = {})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class AppUser extends BaseEntity {

    // region Identity Fields
    @EqualsAndHashCode.Include
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(nullable = false, unique = true)
    String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    String email;

    @NotBlank(message = "First name is mandatory")
    @Column(nullable = false)
    String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Column(nullable = false)
    String lastName;
    // endregion

    // region Profile Fields
    @Setter
    @Column(length = 500)
    String statusMessage;

    @Setter
    String avatarUrl;
    // endregion

    // region Statistics Fields
    @Builder.Default
    int postCount;

    @Builder.Default
    int followerCount;

    @Builder.Default
    int followingCount;
    // endregion

    /**
     * Constructor for creating AppUser instances.
     *
     * @param username  Unique username for the user.
     * @param firstName User's first name.
     * @param lastName  User's last name.
     * @param email     Unique and validated email address.
     */
    public AppUser(final String username, final String firstName, final String lastName, final String email) {
        super();
        validateFields(username, firstName, lastName, email);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Creates a new AppUser with the specified ID.
     *
     * @param id        The UUID to assign to this user
     * @param username  Unique username for the user
     * @param firstName User's first name
     * @param lastName  User's last name
     * @param email     User's email address
     * @return A new AppUser instance
     */
    public static AppUser withId(UUID id, String username, String firstName, String lastName, String email) {
        AppUser user = new AppUser(username, firstName, lastName, email);
        user.setId(id);
        return user;
    }

    /**
     * Validates mandatory fields during user creation.
     *
     * @param username  Username to validate.
     * @param firstName First name to validate.
     * @param lastName  Last name to validate.
     * @param email     Email to validate.
     */
    private static void validateFields(final CharSequence username, final CharSequence firstName, final CharSequence lastName, final CharSequence email) {
        if (null == username || username.isEmpty()) {
            throw new IllegalArgumentException("Username is mandatory");
        }
        if (null == firstName || firstName.isEmpty()) {
            throw new IllegalArgumentException("First name is mandatory");
        }
        if (null == lastName || lastName.isEmpty()) {
            throw new IllegalArgumentException("Last name is mandatory");
        }
        if (null == email || email.isEmpty()) {
            throw new IllegalArgumentException("Email is mandatory");
        }
    }
}
