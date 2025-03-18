package com.projecthub.base.shared.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * BaseEntity provides common fields for all entities and supports domain events.
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Auditable {

    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastModifiedDate;

    /**
     * Returns an unmodifiable list of domain events.
     *
     * @return unmodifiable list of domain events.
     */
    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    @Override
    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public void setLastModifiedDate(final LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * Adds an event to the domain events list.
     *
     * @param event the event to register.
     */
    protected void registerEvent(final Object event) {
        this.domainEvents.add(event);
    }

    /**
     * Clears all domain events.
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    protected static abstract class BaseEntityBuilder<C extends BaseEntity, B extends BaseEntityBuilder<C, B>> {
        protected List<Object> domainEvents = new ArrayList<>();

        protected BaseEntityBuilder() {
        }
    }
}
