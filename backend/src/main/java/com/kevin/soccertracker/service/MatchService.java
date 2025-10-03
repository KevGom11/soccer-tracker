package com.kevin.soccertracker.service;

import com.kevin.soccertracker.domain.Match;
import com.kevin.soccertracker.repo.MatchRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepo matchRepo;

    /** Core windowed fetch using a duration going forward from now (upcoming). */
    public List<Match> getUpcomingMatches(Long teamId, Duration window) {
        Objects.requireNonNull(teamId, "teamId must not be null");
        Duration w = (window == null || window.isNegative() || window.isZero())
                ? Duration.ofHours(24) : window;

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime to = now.plus(w);

        List<Match> matches = matchRepo.findForTeamBetween(teamId, now, to);
        matches.sort(Comparator.comparing(Match::getUtcDate));
        return matches;
    }

    /** Hours lookahead (kept for SubscriptionService compatibility). */
    public List<Match> getUpcomingMatches(Long teamId, int hours) {
        int h = (hours <= 0) ? 24 : hours;
        return getUpcomingMatches(teamId, Duration.ofHours(h));
    }

    /** Days lookahead used by controllers. */
    public List<Match> upcomingMatches(Long teamId, int days) {
        int d = (days <= 0) ? 1 : days;
        return getUpcomingMatches(teamId, Duration.ofDays(d));
    }

    /** Past N days, newest first. */
    public List<Match> recentMatches(Long teamId, int days) {
        int d = (days <= 0) ? 1 : days;
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime from = now.minusDays(d);

        List<Match> matches = matchRepo.findForTeamBetween(teamId, from, now);
        matches.sort(Comparator.comparing(Match::getUtcDate).reversed());
        return matches;
    }
}
