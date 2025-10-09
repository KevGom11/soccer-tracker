package com.kevin.soccertracker.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "session_token")
public class SessionToken {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user; // your existing User entity (maps to app_user)

    @Column(nullable=false)
    private OffsetDateTime expiresAt;

    @Column(nullable=false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    private OffsetDateTime revokedAt;

    public SessionToken() {}
    public SessionToken(UUID token, User user, OffsetDateTime expiresAt) {
        this.token = token; this.user = user; this.expiresAt = expiresAt;
    }

    public UUID getToken() { return token; }
    public void setToken(UUID token) { this.token = token; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(OffsetDateTime revokedAt) { this.revokedAt = revokedAt; }
}
