package com.projecthub.base.school.api.graphql;

import lombok.Data;

@Data
public class SchoolSearchInput {
    private String name;
    private String city;
    private String state;
    private Boolean archived;
}
