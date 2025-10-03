package com.kevin.soccertracker.repo;

import com.kevin.soccertracker.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepo extends JpaRepository<Match, Long> {

    /**
     * Matches for a team (either home or away) within a datetime window (inclusive).
     * Uses explicit JPQL with the actual mapped field name: kickoffAt.
     */
    @Query("""
           select m
           from Match m
           where (m.homeTeam.id = :teamId or m.awayTeam.id = :teamId)
             and m.kickoffAt between :from and :to
           order by m.kickoffAt asc
           """)
    List<Match> findForTeamBetween(@Param("teamId") Long teamId,
                                   @Param("from") ZonedDateTime from,
                                   @Param("to") ZonedDateTime to);

    /**
     * Lookup by external (football-data.org) match id.
     */
    Optional<Match> findByExternalId(Long externalId);
}
