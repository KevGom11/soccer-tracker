package com.kevin.soccertracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.soccertracker.client.SoccerApiClient;
import com.kevin.soccertracker.dto.MatchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiveMatchService {

    private final SoccerApiClient apiClient;
    private final ObjectMapper objectMapper;

    @Value("${footballdata.statusesUpcoming:SCHEDULED,TIMED}")
    private String statusesUpcoming;

    @Value("${footballdata.statusesActive:SCHEDULED,TIMED,IN_PLAY,PAUSED}")
    private String statusesActive;

    /** Up to 12 comps for free plan. Codes or IDs are accepted. */
    @Value("${footballdata.defaultCompetitions:PL,PD,SA,BL1,FL1,CL,ELC,DED,MLI,MLS,CLI,WC}")
    private String defaultCompetitions;

    /* ---------- TEAM-SCOPED (legacy compatibility) ---------- */

    public List<MatchDto> upcomingByTeam(Long teamId, int days) {
        int d = Math.max(1, days);
        LocalDate from = LocalDate.now(ZoneOffset.UTC);
        LocalDate to   = from.plusDays(d);
        String json = apiClient.getTeamMatchesRaw(teamId, from, to, true).block();
        return parseMatches(json);
    }

    public List<MatchDto> recentByTeam(Long teamId, int days) {
        int d = Math.max(1, days);
        LocalDate to   = LocalDate.now(ZoneOffset.UTC);
        LocalDate from = to.minusDays(d);
        String json = apiClient.getTeamMatchesRaw(teamId, from, to, true).block();
        return parseMatches(json);
    }

    /* ---------- MULTI-LEAGUE (new) ---------- */

    public List<MatchDto> upcomingByCompetitions(String competitionsCsv, int days) {
        int d = Math.max(1, days);
        LocalDate from = LocalDate.now(ZoneOffset.UTC);
        LocalDate to   = from.plusDays(d);
        String comps   = (competitionsCsv == null || competitionsCsv.isBlank())
                ? defaultCompetitions : competitionsCsv;
        String json = apiClient.getMatchesRaw(from, to, statusesUpcoming, comps, true).block();
        return parseMatches(json);
    }

    public List<MatchDto> recentByCompetitions(String competitionsCsv, int days) {
        int d = Math.max(1, days);
        LocalDate to   = LocalDate.now(ZoneOffset.UTC);
        LocalDate from = to.minusDays(d);
        String comps   = (competitionsCsv == null || competitionsCsv.isBlank())
                ? defaultCompetitions : competitionsCsv;
        String json = apiClient.getMatchesRaw(from, to, statusesActive, comps, true).block();
        return parseMatches(json);
    }

    /* ---------- JSON → DTO ---------- */

    private List<MatchDto> parseMatches(String json) {
        List<MatchDto> out = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json == null ? "{}" : json);
            JsonNode matches = root.path("matches");
            if (matches.isArray()) {
                Iterator<JsonNode> it = matches.elements();
                while (it.hasNext()) {
                    JsonNode m = it.next();
                    MatchDto dto = toDto(m);
                    if (dto != null) out.add(dto);
                }
            }
        } catch (Exception ignored) {}
        return out;
    }

    private MatchDto toDto(JsonNode m) {
        if (m == null || m.isMissingNode()) return null;

        Long externalId = m.path("id").isNumber() ? m.get("id").asLong() : null;

        String competition = null;
        JsonNode comp = m.path("competition");
        if (!comp.isMissingNode() && comp.hasNonNull("name")) {
            competition = comp.get("name").asText();
        }

        String status = m.hasNonNull("status") ? m.get("status").asText() : null;

        ZonedDateTime utcDate = null;
        if (m.hasNonNull("utcDate")) {
            utcDate = ZonedDateTime.parse(m.get("utcDate").asText());
        }

        String homeTeam = null, awayTeam = null;
        JsonNode home = m.path("homeTeam");
        if (home.hasNonNull("name")) homeTeam = home.get("name").asText();
        JsonNode away = m.path("awayTeam");
        if (away.hasNonNull("name")) awayTeam = away.get("name").asText();

        Integer homeScore = null, awayScore = null;
        JsonNode score = m.path("score").path("fullTime");
        if (score.has("home") && !score.get("home").isNull()) homeScore = score.get("home").asInt();
        if (score.has("away") && !score.get("away").isNull()) awayScore = score.get("away").asInt();

        Long id = externalId; // use API id
        return new MatchDto(id, externalId, competition, status, utcDate, homeTeam, awayTeam, homeScore, awayScore);
    }
}
