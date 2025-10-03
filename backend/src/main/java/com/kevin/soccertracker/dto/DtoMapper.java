package com.kevin.soccertracker.dto;

import com.kevin.soccertracker.domain.Match;
import com.kevin.soccertracker.domain.Subscription;
import com.kevin.soccertracker.domain.Team;

public final class DtoMapper {
    private DtoMapper() {}

    public static TeamDto toDto(Team t) {
        if (t == null) return null;
        return new TeamDto(t.getId(), t.getName(), t.getShortName(), t.getTla(), t.getArea());
    }

    public static MatchDto toDto(Match m) {
        if (m == null) return null;
        return new MatchDto(
                m.getId(),
                m.getExternalId(),
                m.getCompetition(),
                m.getStatus(),
                m.getUtcDate(),
                m.getHomeTeam() != null ? m.getHomeTeam().getName() : null,
                m.getAwayTeam() != null ? m.getAwayTeam().getName() : null,
                m.getHomeScore(),
                m.getAwayScore()
        );
    }

    public static SubscriptionDto toDto(Subscription s) {
        if (s == null) return null;
        return new SubscriptionDto(s.getId(), s.getEmail(), s.getTeamId());
    }
}
