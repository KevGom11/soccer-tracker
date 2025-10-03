package com.kevin.soccertracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.soccertracker.client.SoccerApiClient;
import com.kevin.soccertracker.repo.MatchRepo;
import com.kevin.soccertracker.domain.Match;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchSyncService {

    private final SoccerApiClient api;
    private final MatchRepo matchRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Pulls team matches for the given date window (inclusive), then updates
     * scores/status for any matches we already have in our DB (by externalId).
     *
     * @return number of DB rows updated
     */
    public int syncTeamWindow(long teamId, LocalDate from, LocalDate to, boolean includeActive) {
        String json = api.getTeamMatchesRaw(teamId, from, to, includeActive).block();
        if (json == null || json.isBlank()) {
            log.info("syncTeamWindow: team={} no content", teamId);
            return 0;
        }

        AtomicInteger updated = new AtomicInteger(0);

        try {
            JsonNode root = mapper.readTree(json);
            JsonNode matches = root.get("matches");
            if (matches == null || !matches.isArray()) {
                log.info("syncTeamWindow: team={} no matches array", teamId);
                return 0;
            }

            for (JsonNode n : matches) {
                Long extId = safeLong(n.get("id"));
                if (extId == null) continue;

                matchRepo.findByExternalId(extId).ifPresent(existing -> {
                    // status
                    String status = text(n.get("status"));
                    if (status != null) existing.setStatus(status);

                    // score (prefer fullTime)
                    Integer home = null, away = null;
                    JsonNode score = n.get("score");
                    if (score != null) {
                        JsonNode fullTime = score.get("fullTime");
                        if (fullTime != null) {
                            home = asInt(fullTime.get("home"));
                            away = asInt(fullTime.get("away"));
                        }
                        if (home == null) home = asInt(score.get("home"));
                        if (away == null) away = asInt(score.get("away"));
                    }
                    if (home != null) existing.setHomeScore(home);
                    if (away != null) existing.setAwayScore(away);

                    matchRepo.save(existing);
                    updated.incrementAndGet();
                });
            }
        } catch (Exception e) {
            log.warn("syncTeamWindow: parse/update failed for team={} : {}", teamId, e.toString());
        }
        return updated.get();
    }

    private static Long safeLong(JsonNode n) {
        return (n != null && n.canConvertToLong()) ? n.asLong() : null;
    }

    private static Integer asInt(JsonNode n) {
        return (n != null && n.isInt()) ? n.asInt() : null;
    }

    private static String text(JsonNode n) {
        return (n != null && !n.isNull()) ? n.asText() : null;
    }
}
