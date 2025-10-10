package com.kevin.soccertracker.repo;

import com.kevin.soccertracker.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepo extends JpaRepository<Team, Long> {
    List<Team> findByNameContainingIgnoreCase(String q);


    List<Team> findByLeagueOrderByNameAsc(String league);


    @Query("select distinct t.league from Team t where t.league is not null order by t.league")
    List<String> findDistinctLeagues();


    @Query("select t.league as league, count(t) as cnt from Team t where t.league is not null group by t.league order by t.league")
    List<Object[]> countByLeague();
}
