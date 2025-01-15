package com.projecthub.base.submission.domain.builder;


protected static abstract class SubmissionBuilder<C extends Submission, B extends SubmissionBuilder<C, B>> 
extends BaseEntityBuilder<C, B> {

@Builder.Default
protected ActivationStatus status = ActivationStatus.PENDING;
@Builder.Default
protected SubmissionStatus submissionStatus = SubmissionStatus.PENDING;
@Builder.Default
protected List<DomainEvent> events = new ArrayList<>();

public B validateAndBuild() {
if (content == null || student == null || project == null) {
    throw new IllegalStateException("Required fields must be set");
}
return self();
}
}