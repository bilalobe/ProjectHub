package com.projecthub.base.submission.api.graphql.input;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateSubmissionInput {

    private UUID studentId;
    private UUID projectId;
    private String content;
    private String filePath;
    private boolean isLate;
}
