package com.kevin.soccertracker.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

    public User() {}

    public User(Long id, String email, String name, Instant createdAt) {
        this.id = id;

        this.email = email;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Long id;
        private String password;
        private String email;
        private String name;
        private Instant createdAt;

        public Builder id(Long id) { this.id = id; return this; }



        public Builder email(String email) { this.email = email; return this; }

        public Builder name(String name) { this.name = name; return this; }

        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public User build() { return new User(id,email, name, createdAt); }
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }



    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
