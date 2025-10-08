package com.kevin.soccertracker.dto;
import java.time.ZonedDateTime;
public record MatchDto(
        Long id, Long externalId, String competition, String status, ZonedDateTime utcDate,
        String homeTeam, String awayTeam, Integer homeScore, Integer awayScore
) {}