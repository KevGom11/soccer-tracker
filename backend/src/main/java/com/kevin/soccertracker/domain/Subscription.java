package com.kevin.soccertracker.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "subscription",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "team_id"}))
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


// who is subscribed
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    // which team
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // Reminder settings used by ReminderJob
    @Column(name = "hours_before", nullable = false)
    private Integer hoursBefore = 2;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "last_sent_at")
    private Instant lastSentAt;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

    public Subscription() {}

    public Subscription(Long id, User user, Team team, Integer hoursBefore,
                        Boolean active, Instant lastSentAt, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.team = team;
        this.hoursBefore = (hoursBefore != null) ? hoursBefore : 2;
        this.active = (active != null) ? active : true;
        this.lastSentAt = lastSentAt;
        this.createdAt = createdAt;
    }

    // Manual builder with .email(String) convenience (used in controller)
    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private Long id;
        private User user;
        private Team team;
        private Integer hoursBefore = 2;
        private Boolean active = true;
        private Instant lastSentAt;
        private Instant createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder team(Team team) { this.team = team; return this; }
        public Builder hoursBefore(Integer hoursBefore) { this.hoursBefore = hoursBefore; return this; }
        public Builder active(Boolean active) { this.active = active; return this; }
        public Builder lastSentAt(Instant lastSentAt) { this.lastSentAt = lastSentAt; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }


        public Builder email(String email) {
            if (this.user == null) this.user = new User();
            this.user.setEmail(email);
            return this;
        }

        public Subscription build() {
            return new Subscription(id, user, team, hoursBefore, active, lastSentAt, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public Integer getHoursBefore() { return hoursBefore; }
    public void setHoursBefore(Integer hoursBefore) { this.hoursBefore = hoursBefore; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Instant getLastSentAt() { return lastSentAt; }
    public void setLastSentAt(Instant lastSentAt) { this.lastSentAt = lastSentAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    // Convenience accessors used elsewhere
    public String getEmail() { return user != null ? user.getEmail() : null; }
    public Long getTeamId() { return team != null ? team.getId() : null; }
}
