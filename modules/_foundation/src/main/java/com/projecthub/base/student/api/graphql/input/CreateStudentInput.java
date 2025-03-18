package com.projecthub.base.student.api.graphql.input;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateStudentInput {
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phoneNumber;
    private String emergencyContact;
    private UUID teamId;
}
