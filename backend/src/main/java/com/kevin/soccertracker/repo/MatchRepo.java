package com.kevin.soccertracker.repo;

import com.kevin.soccertracker.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepo extends JpaRepository<Match, Long> {


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

    Optional<Match> findByExternalId(Long externalId);
}
