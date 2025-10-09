package com.kevin.soccertracker.repo;

import com.kevin.soccertracker.domain.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepo extends JpaRepository<Subscription, Long> {
    // Legacy email-based queries (kept for backward compatibility where needed)
    List<Subscription> findByUser_Email(String email);
    Page<Subscription> findByUser_Email(String email, Pageable page);
    boolean existsByUser_Email(String email);
    Optional<Subscription> findFirstByUser_Email(String email);

    // New id-based helpers for authenticated flows
    List<Subscription> findByUser_Id(Long userId);
    Optional<Subscription> findByUser_IdAndTeam_Id(Long userId, Long teamId);
}

