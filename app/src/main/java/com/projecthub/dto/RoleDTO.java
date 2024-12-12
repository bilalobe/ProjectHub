package com.projecthub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for role data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private UUID id;
    private String name;
}