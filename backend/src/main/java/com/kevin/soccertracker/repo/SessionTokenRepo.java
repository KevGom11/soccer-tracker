package com.kevin.soccertracker.repo;

import com.kevin.soccertracker.domain.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SessionTokenRepo extends JpaRepository<SessionToken, UUID> {
    Optional<SessionToken> findByToken(UUID token);
}
