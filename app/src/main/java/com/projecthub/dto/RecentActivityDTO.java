package com.projecthub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for recent activity data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {

    private String timestamp;
    private String activity;
    private String user;
}