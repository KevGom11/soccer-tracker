package com.kevin.soccertracker.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@Table(name = "match")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    // Stored as TIMESTAMPTZ in DB; mapped to ZonedDateTime in Java
    @Column(name = "kickoff_at", nullable = false)
    private ZonedDateTime kickoffAt;

    private String venue;

    @Column(nullable = false)
    private String status = "SCHEDULED"; // SCHEDULED | LIVE | FT

    private Integer homeScore;
    private Integer awayScore;

    // Must be Long to work with seen.add(m.getExternalId())
    @Column(name = "external_id")
    private Long externalId;

    private String competition;

    @Column(name = "external_ref")
    private String externalRef;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

    // ---- Constructors ----
    public Match() {}

    public Match(Long id,
                 Team homeTeam,
                 Team awayTeam,
                 ZonedDateTime kickoffAt,
                 String venue,
                 String status,
                 Integer homeScore,
                 Integer awayScore,
                 Long externalId,
                 String competition,
                 String externalRef,
                 Instant createdAt) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.kickoffAt = kickoffAt;
        this.venue = venue;
        this.status = (status != null) ? status : "SCHEDULED";
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.externalId = externalId;
        this.competition = competition;
        this.externalRef = externalRef;
        this.createdAt = createdAt;
    }

    // ---- Manual Builder (optional, for consistency with other classes) ----
    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private Long id;
        private Team homeTeam;
        private Team awayTeam;
        private ZonedDateTime kickoffAt;
        private String venue;
        private String status = "SCHEDULED";
        private Integer homeScore;
        private Integer awayScore;
        private Long externalId;
        private String competition;
        private String externalRef;
        private Instant createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder homeTeam(Team t) { this.homeTeam = t; return this; }
        public Builder awayTeam(Team t) { this.awayTeam = t; return this; }
        public Builder kickoffAt(ZonedDateTime zdt) { this.kickoffAt = zdt; return this; }
        public Builder venue(String v) { this.venue = v; return this; }
        public Builder status(String s) { this.status = s; return this; }
        public Builder homeScore(Integer s) { this.homeScore = s; return this; }
        public Builder awayScore(Integer s) { this.awayScore = s; return this; }
        public Builder externalId(Long id) { this.externalId = id; return this; }
        public Builder competition(String c) { this.competition = c; return this; }
        public Builder externalRef(String r) { this.externalRef = r; return this; }
        public Builder createdAt(Instant i) { this.createdAt = i; return this; }

        public Match build() {
            return new Match(id, homeTeam, awayTeam, kickoffAt, venue, status,
                    homeScore, awayScore, externalId, competition,
                    externalRef, createdAt);
        }
    }

    // ---- Getters / Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Team getHomeTeam() { return homeTeam; }
    public void setHomeTeam(Team homeTeam) { this.homeTeam = homeTeam; }

    public Team getAwayTeam() { return awayTeam; }
    public void setAwayTeam(Team awayTeam) { this.awayTeam = awayTeam; }

    public ZonedDateTime getKickoffAt() { return kickoffAt; }
    public void setKickoffAt(ZonedDateTime kickoffAt) { this.kickoffAt = kickoffAt; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getHomeScore() { return homeScore; }
    public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }

    public Integer getAwayScore() { return awayScore; }
    public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }

    public Long getExternalId() { return externalId; }
    public void setExternalId(Long externalId) { this.externalId = externalId; }

    public String getCompetition() { return competition; }
    public void setCompetition(String competition) { this.competition = competition; }

    public String getExternalRef() { return externalRef; }
    public void setExternalRef(String externalRef) { this.externalRef = externalRef; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    // Alias used across code (sorting, filters)
    public ZonedDateTime getUtcDate() { return kickoffAt; }
}
