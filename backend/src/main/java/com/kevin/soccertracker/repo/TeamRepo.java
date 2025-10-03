package com.kevin.soccertracker.repo;

import com.kevin.soccertracker.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepo extends JpaRepository<Team, Long> {
    List<Team> findByNameContainingIgnoreCase(String q);
}
