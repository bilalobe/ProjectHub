package com.projecthub.base.submission.api.graphql.input;

import lombok.Data;

@Data
public class UpdateSubmissionInput {
    private String content;
    private String filePath;
}
