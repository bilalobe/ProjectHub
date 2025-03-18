package com.projecthub.base.submission.domain.aggregate;

import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.event.SubmissionDomainEvent;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AggregateRoot
public class SubmissionAggregate {
    private final Submission root;
    private final List<SubmissionDomainEvent> events;
    private final UUID initiatorId;

    private SubmissionAggregate(Submission root, UUID initiatorId) {
        this.root = root;
        this.initiatorId = initiatorId;
        this.events = new ArrayList<>();
    }

    public static SubmissionAggregate create(UUID studentId, UUID projectId, String content, String filePath, boolean isLate, UUID initiatorId) {
        Submission submission = Submission.create(studentId, projectId, content, filePath, isLate);
        return new SubmissionAggregate(submission, initiatorId);
    }

    public static SubmissionAggregate reconstitute(Submission submission, UUID initiatorId) {
        return new SubmissionAggregate(submission, initiatorId);
    }

    public void submit() {
        root.submit();
        events.addAll(root.getDomainEvents());
        root.clearDomainEvents();
    }

    public void grade(int grade, String feedback) {
        root.grade(grade, feedback, initiatorId);
        events.addAll(root.getDomainEvents());
        root.clearDomainEvents();
    }

    public void revoke(String reason) {
        root.revoke(reason, initiatorId);
        events.addAll(root.getDomainEvents());
        root.clearDomainEvents();
    }

    public void addComment(String text) {
        root.addComment(text, initiatorId);
        events.addAll(root.getDomainEvents());
        root.clearDomainEvents();
    }

    public void updateContent(String content, String filePath) {
        root.updateContent(content, filePath);
        events.addAll(root.getDomainEvents());
        root.clearDomainEvents();
    }

    public List<SubmissionDomainEvent> getDomainEvents() {
        return List.copyOf(events);
    }

    public void clearDomainEvents() {
        events.clear();
    }
}
