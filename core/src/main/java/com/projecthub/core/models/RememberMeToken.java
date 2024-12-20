package com.projecthub.core.models;

import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "remember_me_tokens")
public class RememberMeToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String series;

    private String tokenValue;

    @Column(nullable = false)
    private LocalDateTime lastUsed;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
}