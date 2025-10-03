package com.kevin.soccertracker.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "team", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "league"}))
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "tla", length = 3)
    private String tla;

    private String area;

    private String league;
    private String country;

    @Column(name = "external_ref")
    private String externalRef;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

    public Team() {}

    public Team(Long id, String name, String shortName, String tla, String area,
                String league, String country, String externalRef, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.tla = tla;
        this.area = area;
        this.league = league;
        this.country = country;
        this.externalRef = externalRef;
        this.createdAt = createdAt;
    }

    // Manual builder
    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private Long id;
        private String name;
        private String shortName;
        private String tla;
        private String area;
        private String league;
        private String country;
        private String externalRef;
        private Instant createdAt;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder shortName(String shortName) { this.shortName = shortName; return this; }
        public Builder tla(String tla) { this.tla = tla; return this; }
        public Builder area(String area) { this.area = area; return this; }
        public Builder league(String league) { this.league = league; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder externalRef(String externalRef) { this.externalRef = externalRef; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Team build() {
            return new Team(id, name, shortName, tla, area, league, country, externalRef, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }

    public String getTla() { return tla; }
    public void setTla(String tla) { this.tla = tla; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getLeague() { return league; }
    public void setLeague(String league) { this.league = league; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getExternalRef() { return externalRef; }
    public void setExternalRef(String externalRef) { this.externalRef = externalRef; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
