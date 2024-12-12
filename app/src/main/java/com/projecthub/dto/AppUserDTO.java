package com.projecthub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object for the AppUser entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDTO {

    private UUID id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;
}