package com.kevin.soccertracker.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
public class SoccerApiClient {
    private static final Logger log = LoggerFactory.getLogger(SoccerApiClient.class);

    private final WebClient web;
    private final int maxWindowDays;
    private final String defaultUpcomingStatuses;  // e.g., SCHEDULED,TIMED
    private final String defaultActiveStatuses;    // e.g., SCHEDULED,TIMED,IN_PLAY,PAUSED

    public SoccerApiClient(
            @Value("${footballdata.baseUrl:https://api.football-data.org/v4}") String baseUrl,
            @Value("${footballdata.token:}") String token,
            @Value("${footballdata.maxWindowDays:10}") int maxWindowDays,
            @Value("${footballdata.statusesUpcoming:SCHEDULED,TIMED}") String defaultUpcoming,
            @Value("${footballdata.statusesActive:SCHEDULED,TIMED,IN_PLAY,PAUSED}") String defaultActive
    ) {
        this.web = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Auth-Token", token)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.maxWindowDays = maxWindowDays;
        this.defaultUpcomingStatuses = defaultUpcoming;
        this.defaultActiveStatuses = defaultActive;
    }

    /** Existing: team-scoped matches (kept for backwards compatibility). */
    public Mono<String> getTeamMatchesRaw(long teamId, LocalDate from, LocalDate to, boolean includeActive) {
        LocalDate[] bounded = bound(from, to);
        String statuses = includeActive ? defaultActiveStatuses : defaultUpcomingStatuses;

        return web.get()
                .uri(uri -> uri.path("/teams/{id}/matches")
                        .queryParam("dateFrom", bounded[0].toString())
                        .queryParam("dateTo", bounded[1].toString())
                        .queryParam("status", statuses)
                        .queryParam("limit", 200)
                        .queryParam("sort", "utcDate")
                        .build(teamId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class).defaultIfEmpty("")
                                .map(body -> {
                                    String msg = "football-data team matches error %s: %s"
                                            .formatted(resp.statusCode(), body);
                                    log.warn(msg);
                                    return new IllegalStateException(msg);
                                }))
                .bodyToMono(String.class);
    }

    /** NEW: multi-league matches in one call (competitions = CSV of codes or IDs). */
    public Mono<String> getMatchesRaw(LocalDate from, LocalDate to,
                                      String statusesCsv,
                                      String competitionsCsv,
                                      boolean sortByDate) {
        LocalDate[] bounded = bound(from, to);
        return web.get()
                .uri(uri -> {
                    var b = uri.path("/matches")
                            .queryParam("dateFrom", bounded[0].toString())
                            .queryParam("dateTo", bounded[1].toString())
                            .queryParam("limit", 200);
                    if (statusesCsv != null && !statusesCsv.isBlank()) {
                        b.queryParam("status", statusesCsv);
                    }
                    if (competitionsCsv != null && !competitionsCsv.isBlank()) {
                        b.queryParam("competitions", competitionsCsv);
                    }
                    if (sortByDate) b.queryParam("sort", "utcDate");
                    return b.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class).defaultIfEmpty("")
                                .map(body -> {
                                    String msg = "football-data matches error %s: %s"
                                            .formatted(resp.statusCode(), body);
                                    log.warn(msg);
                                    return new IllegalStateException(msg);
                                }))
                .bodyToMono(String.class);
    }

    /** Example helper you already had, kept here for completeness. */
    public Mono<String> teamsInCompetition(String competitionCode) {
        return web.get()
                .uri(uri -> uri.path("/competitions/{code}/teams").build(competitionCode))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class).defaultIfEmpty("")
                                .map(body -> {
                                    String msg = "football-data teamsInCompetition error %s: %s"
                                            .formatted(resp.statusCode(), body);
                                    log.warn(msg);
                                    return new IllegalStateException(msg);
                                }))
                .bodyToMono(String.class);
    }

    private LocalDate[] bound(LocalDate from, LocalDate to) {
        LocalDate f = from == null ? LocalDate.now(ZoneOffset.UTC) : from;
        LocalDate t = to   == null ? f.plusDays(maxWindowDays)     : to;
        if (t.isAfter(f.plusDays(maxWindowDays))) {
            t = f.plusDays(maxWindowDays);
        }
        return new LocalDate[]{f, t};
    }
}
