package com.kevin.soccertracker.repo;

import com.kevin.soccertracker.domain.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepo extends JpaRepository<Subscription, Long> {
    // Existing
    List<Subscription> findByUser_Email(String email);
    Page<Subscription> findByUser_Email(String email, Pageable page);
    boolean existsByUser_Email(String email);
    Optional<Subscription> findFirstByUser_Email(String email);

    // NEW: exact pair lookup for idempotent create
    Optional<Subscription> findByUser_IdAndTeam_Id(Long userId, Long teamId);
}
